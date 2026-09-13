# 第三方与共同开发说明

本项目包含共同开发成果及开源组件，不主张全部代码或素材为单一作者独占创作。原有后端 MIT 许可和版权声明保留在 `backend/LICENSE`；各依赖按其发布包附带的许可证使用。

- Java 与前端依赖以 `pom.xml` 和 `package.json` 为准；下载依赖时应保留它们的许可。
- UniApp、鸿蒙 SDK、TuNiao UI、mp-html 等组件按各自许可和平台要求使用。
- 数字人、语音、PPT、地图、邮件、云存储及模型供应商服务可能需要独立账号与授权。数字人业务调用保留，并以 `VITE_AVATAR_SDK_URL` 实际动态加载部署者取得的 SDK；未配置时显示明确提示，不再使用永久失败的假实现。
- PPT 中的架构图作为项目技术说明展示；第三方商标仍归原权利人所有。演示中的游戏素材和外部服务界面不因此获得独立素材再许可。
- Qwen 基座与历史模型权重不适用代码 MIT 许可。模型卡和数据集卡分别说明来源、限制与发布范围。
- 纯模板合成数据子集采用 `LICENSE-DATA` 中的 CC BY 4.0；该许可不扩展到第三方咨询数据或私人参考材料。

公开仓库未附带原始团队身份表格、合作协议或含身份信息的证书。


## 本次恢复的组件与素材

- `admin/public/docmee-ui-sdk-iframe.min.js`：原项目附带的文多多 iframe SDK，所属原分发明确提供 **GPL-3.0**；完整原文保留于 `admin/licenses/docmee-GPL-3.0.txt`。上游 https://github.com/veasion/aippt-ui-iframe 。根 MIT 不覆盖这一独立组件。具体说明见 `admin/THIRD_PARTY_NOTICES.md`。
- `admin/src/utils/vm-sdk/`、`digital-human/src/utils/vm-sdk/`：项目集成加载器；实际 Avatar SDK 由部署者按供应商条款取得，并用配置 URL 加载。`digital-human/THIRD_PARTY_NOTICES.md` 说明入口接口与资源范围。
- `mobile/static/`、`harmony/entry/src/main/resources/` 等：恢复原项目通用图标、插画和游戏贴图，保留原始文件及可见署名；不统一声称它们是项目原创或 MIT 素材。详细边界见各目录 `ASSET_NOTICES.md`。
- 原移动端图标字体和通用加载动画已经恢复；具名第三方歌曲整曲继续不随此次代码发布。
