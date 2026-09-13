# 学生端与 HarmonyOS 配置

## uni-app 学生端

学生端保留完整参赛工程的页面、service、stores、utils 和静态目录，并合入后续开发版本的功能页面。恢复了预约、数字人形象配置和虚拟展厅页面及其路由；原始提示词资源保留在 `mobile/system_prompt.md`，其中机构和团队身份已经泛化。

1. 在 `mobile` 中使用现有锁文件安装依赖，再用支持 Vue 3 的 HBuilderX 打开。该目录是 HBuilderX 工程，未声称已经完成独立 CLI 整包构建。
2. 在 `mobile/utils/URL.js` 设置自己的后端 HTTP/WebSocket 地址。真机中的 localhost 指手机本身，应替换为自己开发环境可达的服务地址。
3. 在 HBuilderX 中填写自己的应用标识、平台签名和域名白名单。腾讯地图的客户端授权 key 位于 `mobile/common/TengXunMapConstant.js`，应按自己应用的权限和域名限制配置；不要放模型或对象存储的服务端 secret。
4. 远程素材地址位于 `mobile/utils/ossUrl.js` 和相应页面。历史私人 OSS 对象不随代码恢复，应配置自己的公开素材服务；原本已经放在代码中的通用图片已原样恢复。

### 数字人和展厅地址

`mobile/utils/integrations.js` 集中提供公开的网页集成地址，可在应用打开相应页面前设置：

```js
uni.setStorageSync('xinqingPublicIntegrations', {
  digitalHumanUrl: 'https://your-host.example/configuration',
  galleryUrl: 'https://your-gallery.example/'
})
```

这里填写自己部署的网页地址，不是 API secret。数字人开发默认地址为 `http://127.0.0.1:5174/configuration`；真机使用前需改成可达地址。自建虚拟展厅地址默认空，未配置时页面明确提示。网页的 HTTPS、CORS、iframe/webview 允许来源和平台业务域名需对应配置。

UI 使用 `@tuniao/tnui-vue3-uniapp`、`@tuniao/tn-style` 与 `@tuniao/tn-icon` 等已声明依赖；不需要把 node_modules 提交到仓库。原 mobile iconfont.ttf 已恢复，避免图标被通用字体替换后失真。

## HarmonyOS

使用支持项目声明的 HarmonyOS SDK/API 版本的 DevEco Studio 打开 `harmony`，由 IDE 同步 ohpm 依赖。后端入口位于 `harmony/entry/src/main/ets/utils/http.ts`；其他直接网络调用请按实际部署调整 localhost 和示例域名。签名、profile、个人 SDK 路径及生产证书不会随仓库提供。

原 ArkTS 页面和上游 Huawei Apache-2.0 版权头保留。咨询资料中的示例记录仍应由自己的授权后端替换。

## 资源恢复与验证边界

本次对 223 个原始通用图片/SVG/小游戏贴图进行了逐项资源审查并原样恢复；另外恢复了原移动端图标字体和四个彩色圆点的加载动画。包括角色插画、按钮图标、游戏贴图、音乐封面插画以及通用人格类型参考图，没有恢复真实身份照片、证书或用户上传记录。素材来源范围见 `mobile/ASSET_NOTICES.md` 与 `harmony/ASSET_NOTICES.md`。

具名第三方音乐的整曲与歌词没有作为 MIT 资源重新发布；相应播放器和资源引用保留示例音源，部署者可用获得授权的音乐替换。远程私人 OSS 素材仍不在公开包内。

静态语法、页面引用和资源存在性检查不能替代 HBuilderX/DevEco 整包编译和真机测试。本次实际执行的构建与测试结果见 [VALIDATION.md](VALIDATION.md)。
