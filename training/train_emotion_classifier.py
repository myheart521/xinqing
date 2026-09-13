#!/usr/bin/env python3
# -*- coding: utf-8 -*-
import argparse
import inspect
import json
import shutil
from pathlib import Path

import numpy as np
import torch
from datasets import load_dataset
from sklearn.metrics import accuracy_score, f1_score, recall_score
from torch import nn
from transformers import AutoModel, AutoTokenizer, Trainer, TrainingArguments


def build_training_args(kwargs):
    signature = inspect.signature(TrainingArguments.__init__)
    if "eval_strategy" in signature.parameters:
        kwargs["eval_strategy"] = kwargs.pop("evaluation_strategy")
    filtered = {k: v for k, v in kwargs.items() if k in signature.parameters}
    return TrainingArguments(**filtered)


class MultiTaskEmotionModel(nn.Module):
    def __init__(self, base_model, num_emotions, num_risks):
        super().__init__()
        self.encoder = AutoModel.from_pretrained(base_model)
        hidden_size = self.encoder.config.hidden_size
        dropout_prob = getattr(self.encoder.config, "hidden_dropout_prob", 0.1)
        self.dropout = nn.Dropout(dropout_prob)
        self.emotion_classifier = nn.Linear(hidden_size, num_emotions)
        self.risk_classifier = nn.Linear(hidden_size, num_risks)
        self.loss_fn = nn.CrossEntropyLoss()

    def forward(
        self,
        input_ids=None,
        attention_mask=None,
        token_type_ids=None,
        emotion_labels=None,
        risk_labels=None,
        **kwargs,
    ):
        encoder_kwargs = {"input_ids": input_ids, "attention_mask": attention_mask}
        if token_type_ids is not None:
            encoder_kwargs["token_type_ids"] = token_type_ids
        outputs = self.encoder(**encoder_kwargs)
        if hasattr(outputs, "pooler_output") and outputs.pooler_output is not None:
            pooled = outputs.pooler_output
        else:
            pooled = outputs.last_hidden_state[:, 0]
        pooled = self.dropout(pooled)
        emotion_logits = self.emotion_classifier(pooled)
        risk_logits = self.risk_classifier(pooled)
        loss = None
        if emotion_labels is not None and risk_labels is not None:
            loss = self.loss_fn(emotion_logits, emotion_labels) + self.loss_fn(risk_logits, risk_labels)
        return {
            "loss": loss,
            "emotion_logits": emotion_logits,
            "risk_logits": risk_logits,
        }


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--base_model", required=True)
    parser.add_argument("--train_file", required=True)
    parser.add_argument("--validation_file", required=True)
    parser.add_argument("--test_file", required=True)
    parser.add_argument("--label_mapping", required=True)
    parser.add_argument("--output_dir", required=True)
    parser.add_argument("--run_name", required=True)
    parser.add_argument("--max_length", type=int, default=256)
    parser.add_argument("--num_train_epochs", type=float, default=8)
    parser.add_argument("--learning_rate", type=float, default=2e-5)
    parser.add_argument("--per_device_train_batch_size", type=int, default=32)
    parser.add_argument("--per_device_eval_batch_size", type=int, default=64)
    parser.add_argument("--logging_steps", type=int, default=10)
    args = parser.parse_args()

    output_dir = Path(args.output_dir)
    output_dir.mkdir(parents=True, exist_ok=True)
    mapping = json.loads(Path(args.label_mapping).read_text(encoding="utf-8"))
    emotion_labels = list(mapping["emotion_labels"])
    risk_ids = sorted(int(k) for k in mapping["risk_levels"].keys())
    emotion_to_id = {label: idx for idx, label in enumerate(emotion_labels)}
    risk_to_id = {risk: idx for idx, risk in enumerate(risk_ids)}

    tokenizer = AutoTokenizer.from_pretrained(args.base_model, use_fast=True)
    ds = load_dataset(
        "json",
        data_files={
            "train": args.train_file,
            "validation": args.validation_file,
            "test": args.test_file,
        },
    )

    def encode(batch):
        encoded = tokenizer(
            batch["text"],
            truncation=True,
            max_length=args.max_length,
            padding=False,
        )
        encoded["emotion_labels"] = [emotion_to_id[x] for x in batch["emotion_label"]]
        encoded["risk_labels"] = [risk_to_id[int(x)] for x in batch["risk_level"]]
        return encoded

    keep_cols = {"input_ids", "attention_mask", "token_type_ids", "emotion_labels", "risk_labels"}
    ds = ds.map(encode, batched=True)
    remove_cols = [c for c in ds["train"].column_names if c not in keep_cols]
    ds = ds.remove_columns(remove_cols)

    model = MultiTaskEmotionModel(args.base_model, len(emotion_labels), len(risk_ids))

    training_args = build_training_args(
        {
            "output_dir": str(output_dir),
            "run_name": args.run_name,
            "num_train_epochs": args.num_train_epochs,
            "learning_rate": args.learning_rate,
            "per_device_train_batch_size": args.per_device_train_batch_size,
            "per_device_eval_batch_size": args.per_device_eval_batch_size,
            "warmup_ratio": 0.05,
            "weight_decay": 0.01,
            "logging_steps": args.logging_steps,
            "save_strategy": "no",
            "evaluation_strategy": "epoch",
            "load_best_model_at_end": False,
            "report_to": ["tensorboard"],
            "remove_unused_columns": False,
            "label_names": ["emotion_labels", "risk_labels"],
            "fp16": torch.cuda.is_available(),
        }
    )

    def collate(features):
        labels = {
            "emotion_labels": torch.tensor([f.pop("emotion_labels") for f in features], dtype=torch.long),
            "risk_labels": torch.tensor([f.pop("risk_labels") for f in features], dtype=torch.long),
        }
        batch = tokenizer.pad(features, padding=True, return_tensors="pt")
        batch.update(labels)
        return batch

    def compute_metrics(eval_pred):
        preds = eval_pred.predictions
        if isinstance(preds, tuple):
            emotion_logits, risk_logits = preds[:2]
        else:
            emotion_logits, risk_logits = preds["emotion_logits"], preds["risk_logits"]
        labels = eval_pred.label_ids
        emotion_true, risk_true = labels
        emotion_pred = np.argmax(emotion_logits, axis=-1)
        risk_pred = np.argmax(risk_logits, axis=-1)
        high_risk_ids = [risk_to_id[x] for x in risk_ids if x >= 3]
        high_true = np.isin(risk_true, high_risk_ids).astype(int)
        high_pred = np.isin(risk_pred, high_risk_ids).astype(int)
        return {
            "emotion_accuracy": accuracy_score(emotion_true, emotion_pred),
            "emotion_macro_f1": f1_score(emotion_true, emotion_pred, average="macro", zero_division=0),
            "emotion_weighted_f1": f1_score(emotion_true, emotion_pred, average="weighted", zero_division=0),
            "risk_macro_f1": f1_score(risk_true, risk_pred, average="macro", zero_division=0),
            "risk_high_recall": recall_score(high_true, high_pred, zero_division=0),
        }

    trainer = Trainer(
        model=model,
        args=training_args,
        train_dataset=ds["train"],
        eval_dataset=ds["validation"],
        processing_class=tokenizer,
        data_collator=collate,
        compute_metrics=compute_metrics,
    )
    train_result = trainer.train()
    trainer.save_state()
    trainer.save_metrics("train", train_result.metrics)
    test_metrics = trainer.evaluate(ds["test"], metric_key_prefix="test")
    trainer.save_metrics("test", test_metrics)

    torch.save(model.state_dict(), output_dir / "pytorch_model.bin")
    tokenizer.save_pretrained(output_dir)
    config = {
        "base_model": args.base_model,
        "emotion_labels": emotion_labels,
        "risk_ids": risk_ids,
        "emotion_to_id": emotion_to_id,
        "risk_to_id": {str(k): v for k, v in risk_to_id.items()},
        "architecture": "AutoModel + emotion/risk multitask heads",
    }
    (output_dir / "config.json").write_text(json.dumps(config, ensure_ascii=False, indent=2), encoding="utf-8")
    (output_dir / "metrics.json").write_text(
        json.dumps(test_metrics, ensure_ascii=False, indent=2),
        encoding="utf-8",
    )

    best_dir = output_dir.parent / "emotion_classifier_best"
    score_file = best_dir / "metrics.json"
    current_score = float(test_metrics.get("test_risk_high_recall", 0.0))
    current_macro = float(test_metrics.get("test_emotion_macro_f1", 0.0))
    should_replace = True
    if score_file.exists():
        prev = json.loads(score_file.read_text(encoding="utf-8"))
        prev_score = float(prev.get("test_risk_high_recall", 0.0))
        prev_macro = float(prev.get("test_emotion_macro_f1", 0.0))
        should_replace = (current_score, current_macro) > (prev_score, prev_macro)
    if should_replace:
        if best_dir.exists():
            shutil.rmtree(best_dir)
        shutil.copytree(output_dir, best_dir)


if __name__ == "__main__":
    main()
