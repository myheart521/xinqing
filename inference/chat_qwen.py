"""Load the actual project LoRA adapter together with its separately licensed Qwen base."""
import argparse
from pathlib import Path

import torch
from peft import PeftModel
from transformers import AutoModelForCausalLM, AutoTokenizer


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--model-dir", type=Path, required=True, help="Extracted xinqing-qwen-v2-lora folder")
    parser.add_argument("--base-model", default="Qwen/Qwen2.5-3B-Instruct", help="Upstream model ID or local base directory")
    parser.add_argument("--prompt", required=True)
    parser.add_argument("--device", default="cuda" if torch.cuda.is_available() else "cpu")
    parser.add_argument("--max-new-tokens", type=int, default=256)
    args = parser.parse_args()
    tokenizer = AutoTokenizer.from_pretrained(args.model_dir, local_files_only=True)
    dtype = torch.bfloat16 if args.device.startswith("cuda") and torch.cuda.is_bf16_supported() else (
        torch.float16 if args.device.startswith("cuda") else torch.float32)
    base = AutoModelForCausalLM.from_pretrained(args.base_model, dtype=dtype, trust_remote_code=False)
    model = PeftModel.from_pretrained(base, args.model_dir).to(args.device).eval()
    text = tokenizer.apply_chat_template([{"role": "user", "content": args.prompt}], tokenize=False, add_generation_prompt=True)
    inputs = tokenizer(text, return_tensors="pt").to(args.device)
    with torch.inference_mode():
        output = model.generate(**inputs, max_new_tokens=args.max_new_tokens, do_sample=False, pad_token_id=tokenizer.pad_token_id)
    print(tokenizer.decode(output[0, inputs["input_ids"].shape[1]:], skip_special_tokens=True))


if __name__ == "__main__":
    main()
