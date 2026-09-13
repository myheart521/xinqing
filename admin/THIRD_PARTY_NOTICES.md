# 管理端第三方组件

`public/docmee-ui-sdk-iframe.min.js` 从原项目自带的 `aippt-ui-iframe` 分发恢复，该分发包含 GNU GPL v3 许可证；完整原文见 `licenses/docmee-GPL-3.0.txt`。本项目 MIT 声明不覆盖此组件。上游项目：https://github.com/veasion/aippt-ui-iframe 。文多多开放平台：https://docmee.cn/open-platform/ui-sdk 。使用服务需要部署者自己的账户与服务端签发的 token。

数字人 SDK 的原始本地分发未附独立再分发许可，因此保留完整业务调用代码，并通过 `VITE_AVATAR_SDK_URL` 加载部署者取得的 SDK ESM 入口；原来永久抛错的占位类已改为真正的动态加载边界。`public/asr-sdk/` 的录音器和 worker 为原项目集成资源，录音无需浏览器持有供应商长期密钥。

地图 SDK、录音 SDK 和其他供应商组件遵循各自条款；它们不因本仓库 MIT 许可而被重新授权。
