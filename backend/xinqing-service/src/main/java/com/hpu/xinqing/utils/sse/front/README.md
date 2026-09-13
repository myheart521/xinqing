# SSE文件下载器

基于SSE (Server-Sent Events) 的文件传输工具，用于从后端向前端实时传输和下载各种类型的文件。

## 功能特点

- 支持多种文件类型：TXT、Markdown、JSON、XML、CSV等
- 基于SSE技术，实现实时流式传输
- 支持大文件的Base64编码传输
- 提供详细的传输进度反馈
- 自动文件下载或手动触发下载
- 完整的错误处理和日志记录
- 支持WebFlux和MVC两种Spring风格

## 后端实现

### 主要组件

1. `SseManager` - SSE连接管理器，处理连接的建立、维持和关闭
2. `FileTransferService` - 文件传输服务，负责文件生成和传输
3. `SseController` - 控制器，提供RESTful API接口

### API接口

| 路径 | 方法 | 描述 |
|-----|-----|-----|
| `/api/sse1/connect/{userId}` | GET | 创建WebFlux风格的SSE连接 |
| `/api/sse1/connect-mvc/{userId}` | GET | 创建MVC风格的SSE连接 |
| `/api/sse1/disconnect/{userId}` | DELETE | 关闭SSE连接 |
| `/api/sse1/transfer` | POST | 生成并传输文件 |
| `/api/sse1/status/{userId}` | GET | 检查连接状态 |

### 事件类型

| 事件类型 | 描述 |
|--------|-----|
| `CONNECT` | 连接建立成功 |
| `FILE_INFO` | 文件元数据信息 |
| `FILE_DATA` | 文件内容数据 (Base64编码) |
| `DOWNLOAD_READY` | 文件下载准备就绪 |
| `ERROR` | 错误信息 |
| `PROGRESS` | 进度更新 |

## 前端实现

### SseFileDownloader 类

```javascript
const downloader = new SseFileDownloader({
    baseUrl: '/api/sse1',
    debug: true,
    autoDownload: true,
    onConnected: data => console.log('已连接', data),
    onFileData: data => console.log('文件数据', data)
});
```

### 主要方法

| 方法 | 描述 |
|-----|-----|
| `connect(userId)` | 连接到SSE服务器 |
| `disconnect()` | 断开SSE连接 |
| `generateFile(content, fileType, fileName)` | 请求生成并下载文件 |
| `download()` | 手动触发文件下载 |
| `checkStatus()` | 检查连接状态 |

### 事件回调

| 回调 | 描述 |
|-----|-----|
| `onConnected` | 连接成功时触发 |
| `onDisconnected` | 连接断开时触发 |
| `onFileInfo` | 接收到文件信息时触发 |
| `onFileData` | 接收到文件数据时触发 |
| `onDownloadReady` | 文件下载准备就绪时触发 |
| `onError` | 发生错误时触发 |
| `onProgress` | 进度更新时触发 |

## 使用示例

### 后端调用示例

```java
@Autowired
private FileTransferService fileTransferService;

// 在任意服务中生成并发送文件
public void generateAndSendReport(Long userId, String reportContent) {
    fileTransferService.transferMarkdownFile(userId, reportContent, "report.md");
}
```

### 前端使用示例

```javascript
// 创建下载器实例
const downloader = new SseFileDownloader({
    baseUrl: '/api/sse1',
    debug: true
});

// 连接SSE服务器
downloader.connect(userId)
    .then(result => {
        console.log('连接成功', result);
        
        // 生成文件
        return downloader.generateFile(
            '# 测试文档\n\n这是一个测试文档内容。', 
            'MD', 
            'test.md'
        );
    })
    .then(result => {
        console.log('文件生成请求已发送', result);
    })
    .catch(error => {
        console.error('错误', error);
    });
```

## 文件下载演示页面

提供了一个完整的演示页面 `file-download-demo.html`，可用于测试和演示文件下载功能：

1. 设置用户ID并建立连接
2. 输入文件内容和选择文件类型
3. 生成并下载文件
4. 查看实时传输进度和状态日志

## 注意事项

1. 确保后端服务已正确配置SSE相关的异步支持
2. 大文件传输可能需要调整服务器超时设置
3. 对于生产环境，建议添加适当的认证和授权机制
4. 浏览器需要支持SSE和Blob API

## 兼容性

- 支持现代浏览器 (Chrome, Firefox, Safari, Edge)
- 后端要求 Spring Boot 2.x 或更高版本
- 需要 Java 8 或更高版本 