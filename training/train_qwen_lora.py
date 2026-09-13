#!/usr/bin/env python3
# -*- coding: utf-8 -*-
import argparse
import inspect
import json
from pathlib import Path

import torch
from datasets import load_dataset
from peft import LoraConfig, TaskType, get_peft_model
from transformers import (
    AutoModelForCausalLM,
    AutoTokenizer,
    DataCollatorForLanguageModeling,
    Trainer,
    TrainingArguments,
)
from transformers.trainer_utils import get_last_checkpoint


def parse_bool(value):
    if isinstance(value, bool):
        return value
    return str(value).lower() in {"1", "true", "yes", "y"}


def build_training_args(kwargs):
    signature = inspect.signature(TrainingArguments.__init__)
    if "eval_strategy" in signature.parameters:
        kwargs["eval_strategy"] = kwargs.pop("evaluation_strategy")
    return TrainingArguments(**kwargs)


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--model_name_or_path", required=True)
    parser.add_argument("--train_file", required=True)
    parser.add_argument("--validation_file", required=True)
    parser.add_argument("--output_dir", required=True)
    parser.add_argument("--max_seq_length", type=int, default=1024)
    parser.add_argument("--num_train_epochs", type=float, default=3)
    parser.add_argument("--learning_rate", type=float, default=1e-4)
    parser.add_argument("--per_device_train_batch_size", type=int, default=4)
    parser.add_argument("--per_device_eval_batch_size", type=int, default=4)
    parser.add_argument("--gradient_accumulation_steps", type=int, default=4)
    parser.add_argument("--lora_r", type=int, default=16)
    parser.add_argument("--lora_alpha", type=int, default=32)
    parser.add_argument("--lora_dropout", type=float, default=0.05)
    parser.add_argument("--bf16", type=parse_bool, default=True)
    parser.add_argument("--gradient_checkpointing", type=parse_bool, default=True)
    parser.add_argument("--logging_steps", type=int, default=10)
    parser.add_argument("--save_total_limit", type=int, default=3)
    parser.add_argument("--resume_auto", type=parse_bool, default=True)
    args = parser.parse_args()

    output_dir = Path(args.output_dir)
    output_dir.mkdir(parents=True, exist_ok=True)

    tokenizer = AutoTokenizer.from_pretrained(
        args.model_name_or_path,
        trust_remote_code=True,
        use_fast=True,
    )
    if tokenizer.pad_token is None:
        tokenizer.pad_token = tokenizer.eos_token

    ds = load_dataset(
        "json",
        data_files={"train": args.train_file, "validation": args.validation_file},
    )

    def format_row(row):
        text = tokenizer.apply_chat_template(
            row["messages"],
            tokenize=False,
            add_generation_prompt=False,
        )
        return {"text": text}

    def tokenize_row(row):
        return tokenizer(
            row["text"],
            truncation=True,
            max_length=args.max_seq_length,
            padding=False,
        )

    ds = ds.map(format_row, remove_columns=ds["train"].column_names)
    ds = ds.map(tokenize_row, batched=False, remove_columns=["text"])

    dtype = torch.bfloat16 if args.bf16 and torch.cuda.is_available() else torch.float16
    model = AutoModelForCausalLM.from_pretrained(
        args.model_name_or_path,
        torch_dtype=dtype,
        trust_remote_code=True,
        low_cpu_mem_usage=True,
    )
    model.config.use_cache = False
    if args.gradient_checkpointing:
        model.gradient_checkpointing_enable()

    lora_config = LoraConfig(
        task_type=TaskType.CAUSAL_LM,
        r=args.lora_r,
        lora_alpha=args.lora_alpha,
        lora_dropout=args.lora_dropout,
        target_modules=[
            "q_proj",
            "k_proj",
            "v_proj",
            "o_proj",
            "gate_proj",
            "up_proj",
            "down_proj",
        ],
    )
    model = get_peft_model(model, lora_config)
    if torch.distributed.is_available() and torch.distributed.is_initialized():
        if torch.distributed.get_rank() == 0:
            model.print_trainable_parameters()
    else:
        model.print_trainable_parameters()

    training_args = build_training_args(
        {
            "output_dir": str(output_dir),
            "num_train_epochs": args.num_train_epochs,
            "learning_rate": args.learning_rate,
            "per_device_train_batch_size": args.per_device_train_batch_size,
            "per_device_eval_batch_size": args.per_device_eval_batch_size,
            "gradient_accumulation_steps": args.gradient_accumulation_steps,
            "warmup_ratio": 0.03,
            "weight_decay": 0.01,
            "logging_steps": args.logging_steps,
            "save_strategy": "epoch",
            "evaluation_strategy": "epoch",
            "save_total_limit": args.save_total_limit,
            "bf16": bool(args.bf16 and torch.cuda.is_available()),
            "fp16": bool((not args.bf16) and torch.cuda.is_available()),
            "gradient_checkpointing": args.gradient_checkpointing,
            "report_to": ["tensorboard"],
            "remove_unused_columns": False,
            "ddp_find_unused_parameters": False,
        }
    )

    trainer = Trainer(
        model=model,
        args=training_args,
        train_dataset=ds["train"],
        eval_dataset=ds["validation"],
        processing_class=tokenizer,
        data_collator=DataCollatorForLanguageModeling(tokenizer=tokenizer, mlm=False),
    )

    checkpoint = None
    if args.resume_auto and output_dir.exists():
        checkpoint = get_last_checkpoint(str(output_dir))
    train_result = trainer.train(resume_from_checkpoint=checkpoint)
    trainer.save_model(str(output_dir))
    tokenizer.save_pretrained(str(output_dir))

    metrics = train_result.metrics
    metrics["train_samples"] = len(ds["train"])
    trainer.log_metrics("train", metrics)
    trainer.save_metrics("train", metrics)
    trainer.save_state()

    eval_metrics = trainer.evaluate()
    eval_metrics["eval_samples"] = len(ds["validation"])
    trainer.log_metrics("eval", eval_metrics)
    trainer.save_metrics("eval", eval_metrics)
    (output_dir / "eval_report.json").write_text(
        json.dumps(eval_metrics, ensure_ascii=False, indent=2),
        encoding="utf-8",
    )


if __name__ == "__main__":
    main()
