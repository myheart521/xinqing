# Vue3 文件下载器使用文档

本文档介绍如何在 Vue3 应用中使用 SSE 连接下载文件。

## 功能特点

- 支持与后端建立 SSE 连接
- 支持多种文件格式下载（文本、Markdown、Excel、Word）
- 自动处理 Base64 编码的文件数据
- 支持分块传输和进度显示
- 完全支持 Vue3 组合式 API

## 使用步骤

### 1. 安装依赖

本工具不需要额外的第三方依赖，使用浏览器原生 EventSource API。

### 2. 引入下载器

将 `vue-file-downloader.js` 文件复制到您的项目中，通常放在 `src/utils` 或您喜欢的位置。

### 3. 在 Vue 组件中使用

```vue
<script setup>
import { ref, reactive, onUnmounted } from 'vue';
import VueFileDownloader from '@/utils/vue-file-downloader';

// 创建下载器实例
const downloader = reactive(new VueFileDownloader());
const userId = ref(24); // 您的用户ID

// 连接到服务器
async function connect() {
  try {
    await downloader.connect(userId.value);
    console.log('连接成功');
  } catch (error) {
    console.error('连接失败:', error);
  }
}

// 生成并下载文件
async function downloadMarkdown() {
  try {
    const content = '# 这是一个测试标题\n\n这是测试内容。';
    await downloader.generateFile(userId.value, content, 'MD');
  } catch (error) {
    console.error('生成文件失败:', error);
  }
}

// 组件卸载时断开连接
onUnmounted(() => {
  downloader.disconnect();
});
</script>

<template>
  <div>
    <button @click="connect">连接服务器</button>
    <button @click="downloadMarkdown">下载Markdown文件</button>
  </div>
</template>
```

### 4. API 参考

#### VueFileDownloader 类

- **构造函数**：`new VueFileDownloader()`

- **方法**：
  - `connect(userId)`: 建立 SSE 连接
  - `disconnect()`: 断开 SSE 连接
  - `generateFile(userId, content, fileType)`: 生成并下载文件
  - `downloadFile()`: 手动触发下载（通常不需要手动调用）

- **属性**：
  - `connected`: 连接状态
  - `progress`: 下载进度
  - `fileName`: 当前文件名
  - `fileInfo`: 文件信息对象

### 5. 文件类型

支持的文件类型包括：

- `TXT`: 文本文件
- `MD`: Markdown 文件
- `EXCEL`: Excel 文件
- `WORD`: Word 文件

## 完整示例

在项目中包含了一个完整的示例组件 `vue-file-download-example.vue`，您可以参考此组件了解如何集成和使用下载器。

## 常见问题

1. **连接失败**
   
   确保后端服务器已正确配置 SSE 端点，并且用户 ID 有效。

2. **文件下载不成功**

   检查浏览器控制台是否有错误信息，确保已成功接收文件数据。

3. **如何在实际项目中集成**

   一般建议将下载功能封装为 Composable 或 Pinia Store，方便在多个组件中重用。

## 与后端集成

确保后端已实现以下接口：

- `/dev-api/sse/connect/{userId}`: 建立 SSE 连接
- `/dev-api/sse/generate`: 生成文件的接口

后端应该发送以下类型的 SSE 事件：

- `CONNECT`: 连接成功
- `FILE_INFO`: 文件信息
- `FILE_DATA_BASE64`: Base64 编码的文件数据
- `DOWNLOAD_READY`: 下载准备就绪
- 文件类型事件（`TEXT`、`MARKDOWN`、`MD`、`EXCEL`、`WORD`） 