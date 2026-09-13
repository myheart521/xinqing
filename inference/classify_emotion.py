"""Run the exported RBT3 emotion/risk classifier without downloading a base model."""
import argparse
import json
from pathlib import Path

import torch
from safetensors.torch import load_file
from transformers import AutoConfig, AutoModel, AutoTokenizer


class EmotionRiskModel(torch.nn.Module):
    def __init__(self, encoder_config, num_emotions, num_risks):
        super().__init__()
        self.encoder = AutoModel.from_config(encoder_config)
        self.dropout = torch.nn.Dropout(getattr(encoder_config, "hidden_dropout_prob", 0.1))
        self.emotion_classifier = torch.nn.Linear(encoder_config.hidden_size, num_emotions)
        self.risk_classifier = torch.nn.Linear(encoder_config.hidden_size, num_risks)

    def forward(self, **inputs):
        result = self.encoder(**inputs)
        pooled = result.pooler_output if result.pooler_output is not None else result.last_hidden_state[:, 0]
        pooled = self.dropout(pooled)
        return self.emotion_classifier(pooled), self.risk_classifier(pooled)


def load_model(model_dir, device="cpu"):
    model_dir = Path(model_dir)
    settings = json.loads((model_dir / "model_config.json").read_text(encoding="utf-8"))
    tokenizer = AutoTokenizer.from_pretrained(model_dir, local_files_only=True)
    config = AutoConfig.from_pretrained(model_dir, local_files_only=True)
    model = EmotionRiskModel(config, len(settings["emotion_labels"]), len(settings["risk_ids"]))
    model.load_state_dict(load_file(str(model_dir / "model.safetensors")), strict=True)
    model.to(device).eval()
    return model, tokenizer, settings


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--model-dir", type=Path, required=True)
    parser.add_argument("--text", required=True)
    parser.add_argument("--device", default="cpu")
    parser.add_argument("--max-length", type=int, default=256)
    args = parser.parse_args()
    model, tokenizer, settings = load_model(args.model_dir, args.device)
    inputs = tokenizer(args.text, return_tensors="pt", truncation=True, max_length=args.max_length)
    inputs = {k: v.to(args.device) for k, v in inputs.items()}
    with torch.inference_mode():
        emotion_logits, risk_logits = model(**inputs)
    emotion = emotion_logits.softmax(-1)[0]
    risk = risk_logits.softmax(-1)[0]
    emotion_id, risk_id = int(emotion.argmax()), int(risk.argmax())
    print(json.dumps({"model": "xinqing-rbt3-emotion-risk", "emotion": settings["emotion_labels"][emotion_id],
                      "emotion_score": float(emotion[emotion_id]), "risk_level": settings["risk_ids"][risk_id],
                      "risk_score": float(risk[risk_id]), "clinical_diagnosis": False}, ensure_ascii=False))


if __name__ == "__main__":
    main()
