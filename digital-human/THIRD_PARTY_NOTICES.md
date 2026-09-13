# 数字人组件说明

原工程使用讯飞数字人 Avatar SDK。原始本地 SDK 分发未附独立再分发许可，公开代码通过 `VITE_AVATAR_SDK_URL` 加载部署者依法取得的 ESM 入口，保留配置、事件、语音和动作等调用实现。入口模块必须导出默认构造器、`PlayerEvents` 与 `SDKEvents`。不再使用永久抛错的空实现。

`public/asr-sdk/` 保留原录音器与 AudioWorklet/Worker 资源；音频识别授权应由服务端生成。Vite 标志 SVG 保留其原始通用图形。相关供应商服务需要部署者自己的服务账户。
