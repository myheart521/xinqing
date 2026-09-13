# 学生端与 HarmonyOS 配置

本发布包含 uni-app 学生端的 108 个 Vue 文件及其服务、状态管理和页面代码，以及 HarmonyOS 客户端的 186 个 ArkTS 文件。完整页面清单与源码一起保留。

## uni-app 学生端

1. 在 `mobile` 目录执行 `npm ci --legacy-peer-deps`，再使用支持 Vue 3 的 HBuilderX 打开该目录。该工程采用 HBuilderX 工程布局，尚未提供经验证的独立 CLI 构建命令。
2. 在 `mobile/utils/URL.js` 配置自己的后端地址。发布默认值为本机 `http://localhost:8080`；手机或模拟器访问宿主机时需改为自己的开发网络地址，并在后端配置相应 CORS。
3. 使用微信小程序或 App 打包时，在 HBuilderX 中填写自己的应用标识和签名配置。地图功能需要自己的授权客户端 Key；入口为 `mobile/common/TengXunMapConstant.js`，原有 Key 已移除。
4. `mobile/utils/ossUrl.js` 和部分页面使用 `assets.example.invalid` 作为历史远程素材的占位域名。接入自己有权使用的素材服务后替换这些地址。

缺失的 `uni_modules/tuniaoui-vue3` 引用已改用 package.json 中声明的 `@tuniao/tnui-vue3-uniapp` npm 包；全局样式来自 `@tuniao/tn-style` 与 `@tuniao/tn-icon`。依赖由 npm 安装，发布不附带 node_modules。

## HarmonyOS

1. 用兼容 HarmonyOS 5.0.0 / API 12 的 DevEco Studio 打开 `harmony`，由 IDE 同步 ohpm 依赖。
2. 在 `harmony/entry/src/main/ets/utils/http.ts` 配置自己的后端地址；其他直接网络调用可搜索 `localhost` 与 `example.invalid`，按实际服务替换。
3. 在 DevEco Studio 为自己的设备配置应用签名。公开工程不含签名证书、私钥、profile、个人 SDK 路径或自动签名凭据。
4. 咨询中心和人员模型中的静态资料为示例，正式资料应来自已授权后端。

## 资源与验证范围

原始图片、头像、图标、动画、音乐、歌词和视频没有随本次源码发布。对应文件改为新生成的中性图片、占位图标字体、一秒静音音频、纯色视频或简单歌词占位，以保留资源引用。原始内容保留在维护者的私人原件中；界面因此与历史截图不同。需要外部素材的地址仍需配置。

发布前检查包括：JSON / JSON5 可解析性、页面清单对应文件、相对源码导入、HarmonyOS `$rawfile` 资源引用、Vue SFC 与 JS/TS 语法，以及密钥扫描。WXS 单独脚本按 uni-app 扩展处理。尚未在 HBuilderX 或 DevEco Studio 中完成整包编译、真机调试或端到端业务验证；这些静态检查不能替代平台构建。

第三方组件仍适用各自许可证，包括图鸟 UI、mp-html、Prism、Vue 和带 Huawei Apache-2.0 版权头的 HarmonyOS 示例衍生代码。腾讯地图 SDK 的使用还需遵守上游 SDK 条款。仓库主许可证不替代这些条款；保留源码中的上游版权声明。
