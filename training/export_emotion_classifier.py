"""Export a historical RBT3 multitask training checkpoint for the public inference loader."""
import argparse
import json
from pathlib import Path

import torch
from safetensors.torch import save_file
from transformers import AutoConfig, AutoTokenizer


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--checkpoint", type=Path, required=True)
    parser.add_argument("--base-model", default="hfl/rbt3")
    parser.add_argument("--output-dir", type=Path, required=True)
    args = parser.parse_args()
    if args.output_dir.exists():
        parser.error("output directory already exists; choose a new path")
    settings = json.loads((args.checkpoint / "config.json").read_text(encoding="utf-8"))
    state = torch.load(args.checkpoint / "pytorch_model.bin", map_location="cpu", weights_only=True)
    config = AutoConfig.from_pretrained(args.base_model)
    config._name_or_path = ""
    tokenizer = AutoTokenizer.from_pretrained(args.checkpoint)
    args.output_dir.mkdir(parents=True)
    config.save_pretrained(args.output_dir)
    tokenizer.save_pretrained(args.output_dir)
    settings["base_model"] = "hfl/rbt3"
    (args.output_dir / "model_config.json").write_text(json.dumps(settings, ensure_ascii=False, indent=2), encoding="utf-8")
    save_file({k: v.detach().contiguous() for k, v in state.items()}, str(args.output_dir / "model.safetensors"), metadata={"format": "pt"})


if __name__ == "__main__":
    main()
