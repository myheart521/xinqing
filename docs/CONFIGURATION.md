# 配置与部署说明

## 环境要求

- 后端：JDK 17、Maven、MySQL 8、Redis；使用消息功能还需要 RabbitMQ。
- 教师端与数字人界面：Node.js 20.19+ 或兼容的 Node.js 22、npm。
- 学生端：HBuilderX/UniApp 与对应平台 SDK；鸿蒙端需要 DevEco Studio 和项目声明的 HarmonyOS SDK。
- AI、对象存储、邮件、地图、语音和数字人使用自己的服务与授权，公开包不提供共享账号。

## 配置方式

后端通过 Spring 的环境变量占位符与 `System.getenv` 读取配置。先阅读根目录 `.env.example`，然后在终端、IDE Run Configuration 或容器环境设置变量。把 `.env` 放在根目录不会自动使 Spring Boot 加载它。

PowerShell 示例（值由你本地填写，不要提交）：

```powershell
$env:SPRING_DATASOURCE_USERNAME = 'your_database_user'
$env:SPRING_DATASOURCE_PASSWORD = 'your_database_password'
$env:SPRING_DATA_REDIS_HOST = 'localhost'
$env:SPRING_DATA_REDIS_PASSWORD = 'your_redis_password'
$env:SPRING_AI_OPENAI_BASE_URL = 'http://localhost:11434/v1'
$env:SPRING_AI_OPENAI_MODEL = 'your_installed_model'
# 其他被启用的集成也需按下表设置。
```

MySQL 建议先自行创建 `xinqing` 空库，再导入 `database/schema.sql`。结构中没有 INSERT，也没有默认管理员、历史用户、会话和测评数据。菜单权限与业务初始数据需要按自己的实例设置。

## 后端变量清单

变量未提供时，一些自动装配组件会导致应用启动失败。编译成功不等于已完成全部服务初始化。非必需集成可在自己的部署分支中关闭对应组件。

| 环境变量 | 对应属性或用途 | 默认值/要求 |
| --- | --- | --- |
| `BAIDU_MAP_API_KEY` | `baidu.map.api.key` | `必须自行设置` |
| `DASHSCOPE_API_KEY` | `Java 外部服务集成` | `必须自行设置` |
| `DASHSCOPE_APP_ID` | `Java 外部服务集成` | `必须自行设置` |
| `LANGCHAIN4J_COMMUNITY_DASHSCOPE_CHAT_MODEL_API_KEY` | `langchain4j.community.dashscope.chat-model.api-key` | `必须自行设置` |
| `LANGCHAIN4J_COMMUNITY_DASHSCOPE_EMBEDDING_MODEL_API_KEY` | `langchain4j.community.dashscope.embedding-model.api-key` | `必须自行设置` |
| `LANGCHAIN4J_COMMUNITY_DASHSCOPE_STREAMING_CHAT_MODEL_API_KEY` | `langchain4j.community.dashscope.streaming-chat-model.api-key` | `必须自行设置` |
| `MYSQL_DATABASE` | `mysql.database` | `xinqing` |
| `MYSQL_HOST` | `mysql.host` | `localhost` |
| `MYSQL_PASSWORD` | `mysql.password` | `必须自行设置` |
| `MYSQL_USER` | `mysql.user` | `必须自行设置` |
| `SA_TOKEN_JWT_SECRET_KEY` | `sa-token.jwt-secret-key` | `必须自行设置` |
| `SEARCH_API_KEY` | `search.api-key` | `必须自行设置` |
| `SMTP_ACCOUNT` | `Java 外部服务集成` | `必须自行设置` |
| `SMTP_HOST` | `Java 外部服务集成` | `必须自行设置` |
| `SMTP_PASSWORD` | `Java 外部服务集成` | `必须自行设置` |
| `SPRING_AI_OPENAI_API_KEY` | `spring.ai.openai.api-key` | `必须自行设置` |
| `SPRING_AI_OPENAI_BASE_URL` | `spring.ai.openai.base-url` | `http://localhost:11434/v1` |
| `SPRING_AI_OPENAI_MODEL` | `spring.ai.openai.chat.options.model` | `qwen2.5:3b` |
| `SPRING_DATASOURCE_PASSWORD` | `spring.datasource.password` | `必须自行设置` |
| `SPRING_DATASOURCE_URL` | `spring.datasource.url` | `jdbc:mysql://localhost:3306/xinqing?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai` |
| `SPRING_DATASOURCE_USERNAME` | `spring.datasource.username` | `必须自行设置` |
| `SPRING_DATA_REDIS_HOST` | `spring.data.redis.host` | `localhost` |
| `SPRING_DATA_REDIS_PASSWORD` | `spring.data.redis.password` | `必须自行设置` |
| `SPRING_RABBITMQ_HOST` | `spring.rabbitmq.host` | `localhost` |
| `SPRING_RABBITMQ_PASSWORD` | `spring.rabbitmq.password` | `必须自行设置` |
| `SPRING_RABBITMQ_USERNAME` | `spring.rabbitmq.username` | `必须自行设置` |
| `XINQING_ALIOSS_ACCESS_KEY_ID` | `xinqing.alioss.access-key-id` | `必须自行设置` |
| `XINQING_ALIOSS_ACCESS_KEY_SECRET` | `xinqing.alioss.access-key-secret` | `必须自行设置` |
| `XINQING_ALIOSS_BUCKET_NAME` | `xinqing.alioss.bucket-name` | `必须自行设置` |
| `XINQING_ALIOSS_ENDPOINT` | `xinqing.alioss.endpoint` | `必须自行设置` |
| `XINQING_HUAWEI_IOT_DEVICE_ID` | `xinqing.huawei.iot.device-id` | `必须自行设置` |
| `XINQING_HUAWEI_IOT_ENDPOINT` | `xinqing.huawei.iot.endpoint` | `必须自行设置` |
| `XINQING_HUAWEI_IOT_IAM_ACCOUNT` | `xinqing.huawei.iot.IAM-account` | `必须自行设置` |
| `XINQING_HUAWEI_IOT_PASSWORD` | `xinqing.huawei.iot.password` | `必须自行设置` |
| `XINQING_HUAWEI_IOT_PROJECT_ID` | `xinqing.huawei.iot.project-id` | `必须自行设置` |
| `XINQING_HUAWEI_IOT_TOKEN_URL` | `xinqing.huawei.iot.token-url` | `必须自行设置` |
| `XINQING_HUAWEI_IOT_USERNAME` | `xinqing.huawei.iot.username` | `必须自行设置` |
| `XINQING_WECHAT_APPID` | `xinqing.wechat.appid` | `必须自行设置` |
| `XINQING_WECHAT_SECRET` | `xinqing.wechat.secret` | `必须自行设置` |

其中 `DASHSCOPE_API_KEY` 用于代码中的百炼调用，`DASHSCOPE_APP_ID` 为自行创建的百炼应用；LangChain4j 的自动装配变量是另一组配置入口。`SMTP_ACCOUNT`、`SMTP_HOST` 和 `SMTP_PASSWORD` 对应自己的发信账号、SMTP 主机和授权凭据。不要把邮箱授权码当作普通登录密码复用。

## 前端连接

教师端与数字人目录提供 `.env.example`。复制为本地 `.env.local` 后可设置 `VITE_API_BASE_URL`、`VITE_WS_URL` 等公开连接信息。开发默认指向本机后端；部署 HTTPS 页面时应配置 HTTPS/WSS，并设置自己的反向代理。

Vite 变量会被编入浏览器文件，只能放公开地址和公钥。**大模型 API Key、OSS Secret、Redis 密码、邮件凭据、私钥和语音签名 Secret 只能留在服务端。**

RSA 登录工具只接受 `VITE_LOGIN_PUBLIC_KEY` 公钥；客户端私钥与解密能力已去除。语音识别直连签名入口不再持有厂商密钥，会提示需要服务端短期授权。数字人 SDK 使用明确报错的占位边界；应按厂商许可安装 SDK，再实现服务端会话授权。PPT API、地图等可选集成需自行完成授权和代理。

学生端和鸿蒙端已移除旧签名、平台 App ID、私有云地址与密钥。需要在各自开发工具中填写自己的应用标识与签名，按照端内配置说明连接后端。

## 构建与运行

在 `backend/` 中执行：

```bash
mvn -DskipTests package
java -jar xinqing-service/target/xinqing-service-0.0.1-SNAPSHOT.jar
```

后一个命令要求先配置并启动所需的数据库、缓存及服务。没有连接生产实例进行本次验证。

在 `admin/` 中执行：

```bash
npm install
npm run dev
npm run pure-build
```

`pure-build` 为 Vite 打包；`build` 还包含 TypeScript 检查。本次具体结果以 `VALIDATION.md` 为准。

## 模型与资源

可以接入自己的兼容模型 API，或按 Ollama 支持的方式部署取得使用权的模型。仓库不提供历史 Qwen 微调权重。不要把公开合成子集误当成原完整训练集，参见模型卡和数据集卡。

历史 OSS 图片和用户资源地址已移除；部分界面使用中性占位素材。部署时请使用你有权使用的图片、音视频和文件存储。原始生产快照、密钥与模型缓存没有放入仓库或 Git 历史。
