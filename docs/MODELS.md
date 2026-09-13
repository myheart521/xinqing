# 下载与运行实际模型

两个模型文件均位于 [v1.1.0 Release](https://github.com/myheart521/xinqing/releases/tag/v1.1.0)，每个文件小于 2 GiB：

| 文件 | 用途 |
| --- | --- |
| [xinqing-qwen-v2-lora.tar.gz](https://github.com/myheart521/xinqing/releases/download/v1.1.0/xinqing-qwen-v2-lora.tar.gz) | 心理支持问答 LoRA；另行下载 Qwen2.5-3B-Instruct 基座 |
| [xinqing-rbt3-emotion-risk.tar.gz](https://github.com/myheart521/xinqing/releases/download/v1.1.0/xinqing-rbt3-emotion-risk.tar.gz) | 完整情绪 / 风险分类器；无需额外基座下载 |

包大小、权重和压缩包 SHA-256 见 [model_release_manifest.json](../model_release_manifest.json)。建议使用独立 Python 3.10+ 环境，安装 [requirements-models.txt](../requirements-models.txt)。已验证运行版本为 PyTorch 2.6.0、Transformers 5.8.0、PEFT 0.19.1；CUDA 用户应先按 PyTorch 官方说明安装适合自己的驱动和 PyTorch 构建。

```bash
python -m venv .venv
# 激活自己的虚拟环境后：
python -m pip install -r requirements-models.txt
mkdir models
tar -xzf xinqing-qwen-v2-lora.tar.gz -C models
tar -xzf xinqing-rbt3-emotion-risk.tar.gz -C models
```

## 心理支持问答

```bash
python inference/chat_qwen.py --model-dir models/xinqing-qwen-v2-lora --prompt "最近学习压力很大"
```

首次运行会从 `Qwen/Qwen2.5-3B-Instruct` 下载基座；请阅读并遵守上游 Qwen RESEARCH 许可。已下载时可通过 `--base-model /path/to/Qwen2.5-3B-Instruct` 指定自己的目录。默认有 CUDA 则使用 GPU，否则使用 CPU；可显式指定 `--device cpu` 或 `--device cuda:0`。CPU 推理较慢且使用 float32，需要为基座及运行开销预留充足内存。建议使用具有至少 10 GB 空闲显存的兼容 GPU。

适配器本身不是完整基座，不能直接作为 GGUF 或只含一个模型文件的独立模型加载。压缩包内部的 infer.py 提供相同入口；在包目录中使用 `python infer.py --model-dir . --prompt "最近学习压力很大"`。

## 情绪与风险分类

```bash
python inference/classify_emotion.py --model-dir models/xinqing-rbt3-emotion-risk --text "最近学习压力很大"
```

此入口从包内 config.json 构造编码器，并严格加载完整 safetensors 权重；model_config.json 保存两个分类头的标签。输出分数为模型概率，不是诊断结论，也不能当作真实危机概率。

## 训练与历史复现范围

训练入口为 [train_qwen_lora.py](../training/train_qwen_lora.py) 和 [train_emotion_classifier.py](../training/train_emotion_classifier.py)，用 `--help` 查看输入、输出和参数。所有数据 / 模型位置由命令行传入，不包含机器专用路径或凭据。调用示例：

```bash
python training/train_qwen_lora.py --model_name_or_path Qwen/Qwen2.5-3B-Instruct --train_file train.jsonl --validation_file dev.jsonl --output_dir outputs/qwen --num_train_epochs 2 --learning_rate 5e-5 --lora_r 32 --lora_alpha 64
```

RBT3 历史训练入口输出 PyTorch state_dict 与标签配置。需要使用本仓库的新推理入口时，运行 `python training/export_emotion_classifier.py --checkpoint outputs/rbt3 --output-dir models/my-rbt3` 转换为 safetensors 发布布局；转换器通过 weights_only=True 读取张量，不加载训练参数 pickle。

这只说明训练入口的调用方式。完整历史训练使用 200,000 条混合数据以及原有验证划分；当前公开的 114,900 条合成子集不能单独逐步复现该训练运行。新训练输出是新的实验，不应冒充本次发布权重。

## 版本选择

本次采用有完整训练记录且由当前服务配置使用的 Qwen V2 适配器和 RBT3 最优分类器。旧版 EmoChat Q8_0 GGUF 为 3,285,475,584 字节，相关目录仅有 safetensors 分片索引而缺少对应分片；它没有作为当前版本重新上传。旧版三分类 BERT 和七分类 RoBERTa 与本次 18 情绪 / 5 风险输出任务不同，亦未混入当前分类器包。

具体参数、数据来源、许可和验证局限见 [MODEL_CARD.md](../MODEL_CARD.md)。
