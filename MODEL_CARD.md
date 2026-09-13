# 心晴模型卡

Built with Qwen.

v1.1.0 提供项目实际微调的 **Qwen V2 LoRA 问答适配器**和 **RBT3 情绪 / 风险分类器**。下载与运行步骤见 [模型使用说明](docs/MODELS.md)。权重放在 GitHub Release，仓库保留推理、训练入口和说明。

## Qwen V2 问答适配器

| 项目 | 内容 |
| --- | --- |
| 下载 | [xinqing-qwen-v2-lora.tar.gz](https://github.com/myheart521/xinqing/releases/download/v1.1.0/xinqing-qwen-v2-lora.tar.gz) |
| 基座 | [Qwen/Qwen2.5-3B-Instruct](https://huggingface.co/Qwen/Qwen2.5-3B-Instruct)，需要另外下载 |
| 结构 | Qwen2ForCausalLM，36 层，hidden size 2048，16 个注意力头，2 个 KV 头 |
| 适配方式 | LoRA，r=32，alpha=64，dropout=0.05，bias=none |
| 目标模块 | q_proj、k_proj、v_proj、o_proj、gate_proj、up_proj、down_proj |
| 实际权重 | adapter_model.safetensors；504 个张量，59,867,136 个参数，239,536,272 字节 |
| 权重 SHA-256 | `3c5981ef4b98f7003c62f4d220c35448f9d9eae8773d33964bf18ef9b2ec9731` |
| 训练记录 | 200,000 条训练、200 条验证、400 条测试；2 epoch，6,250 step，4 张 A100，有效 batch size 64，学习率 5e-5，最大序列长度 1,024，bf16 |
| 归档指标 | train loss 0.899543；eval loss 0.139233；训练耗时 37,763.378 秒 |

发布权重与服务器保存的最终适配器 SHA-256 相同。配置中的机器专用路径改为公开的基座名称。压缩包带 tokenizer、chat template、推理入口、依赖及 Qwen 许可 / NOTICE，不含基座权重、优化器或训练日志。

## RBT3 情绪与风险分类器

| 项目 | 内容 |
| --- | --- |
| 下载 | [xinqing-rbt3-emotion-risk.tar.gz](https://github.com/myheart521/xinqing/releases/download/v1.1.0/xinqing-rbt3-emotion-risk.tar.gz) |
| 基座 / 结构 | hfl/rbt3；3 层 BERT 编码器及独立情绪、风险分类头 |
| 输出 | 18 个情绪标签，5 个风险等级（0—4）；标签顺序保存在 model_config.json |
| 实际权重 | model.safetensors；59 个张量，38,494,487 个参数，153,984,868 字节 |
| 权重 SHA-256 | `d827c0afd14b6c0bd6a7fc7b8f08e26116468844b7f09ae42dfaea520cd11933` |
| 版本 | 历史运行 rbt3_custom_v1；由服务器评估脚本选为 emotion_classifier_best |
| 训练记录 | 2,400 条训练、200 条验证、400 条测试，8 epoch；最大长度 256 |
| 归档测试指标 | emotion macro F1 1.0；risk macro F1 0.877040；高风险召回率 1.0 |

发布包包含完整编码器和两个分类头、配置、标签、tokenizer，推理时无需下载基座。原始 PyTorch state_dict 通过 weights_only=True 读取后转换为 safetensors，逐张量比对值完全一致。归档指标来自规模较小且同源模板较多的测试集，不能当作真实学生或临床场景的性能保证。

## 数据来源与重新核查

Qwen V2 的 200,000 条样本包括 114,900 条独立模板扩展、3,100 条旧版自建样本，以及 CPsyCounD 20,552 条、Psy-Insight 1,699 条和 SMILE 59,749 条转换样本。第三方数据遵循各自上游条款，本次不将它们打包重新分发。

针对旧版中的 300 条“聊天参考派生”样本，本次检查实际生成代码并逐条核对全部用户 / 助手文本：生成函数只接收类别字典，所有输出都与固定的通用模板、子话题和情绪词精确匹配；没有把原始聊天文字传入生成函数，也未发现私人特有叙事、姓名或联系方式。这一来源链支持将这些样本与原始私人聊天区分。

这项检查不是差分隐私证明，也没有进行模型提取或成员推断基准测试。原始聊天、处理明细、提示日志及用户记录仍不公开。数据集发布范围见 [DATASET_CARD.md](DATASET_CARD.md)。

## 实际验证与限制

发布前使用实际 Qwen 适配器及指定基座完成一次独立生成验证；RBT3 转换后权重经过严格 state_dict 加载与实际前向推理验证。验证的是文件可加载和推理链路，不是新的效果评测。

模型研究目标是校园心理支持表达、拒答边界和工具调用格式。它们不能替代诊断、治疗、用药决策或真人危机处置。训练脚本没有仅对助手回答计算损失的掩码；部分工具数据独立抽样问题文字与参数，可能不匹配。发布不声称临床验证、可靠自动预约或自动危机处置能力。

## 许可

Qwen 适配器遵循随包提供的 **Qwen RESEARCH LICENSE AGREEMENT（2024-09-19）**及 NOTICE：允许符合条款的非商业研究 / 评估使用和再分发；商业使用需按上游条款另行申请。包内保留 Built with Qwen、归属及修改说明。它不是 MIT 或 Apache-2.0 权重许可。

RBT3 上游模型卡声明 Apache-2.0；本次分类器修改及导出按 Apache-2.0 发布，包内保留许可证与修改说明。仓库主许可证不覆盖 Qwen 权重、第三方语料或其他上游材料。
