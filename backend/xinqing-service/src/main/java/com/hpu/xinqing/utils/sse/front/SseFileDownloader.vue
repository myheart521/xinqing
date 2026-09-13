<template>
  <div class="container">
    <h1>SSE文件下载演示</h1>
    
    <div class="section">
      <div class="section-title">1. 连接设置</div>
      <div class="row">
        <label for="userId">用户ID：</label>
        <input type="number" id="userId" v-model="userId" min="1" :disabled="connected" />
      </div>
      <div class="row">
        <label for="baseUrl">API基础URL：</label>
        <input type="text" id="baseUrl" v-model="baseUrl" :disabled="connected" />
      </div>
      <div class="row">
        <label for="apiPrefix">API前缀：</label>
        <input type="text" id="apiPrefix" v-model="apiPrefix" :disabled="connected" placeholder="例如：/dev-api，不需要则留空" />
      </div>
      <div class="row">
        <label></label>
        <div>
          <button :disabled="connected" @click="connect">建立连接</button>
          <button :disabled="!connected" @click="disconnect">断开连接</button>
          <button @click="checkStatus">检查状态</button>
        </div>
      </div>
    </div>
    
    <div class="section">
      <div class="section-title">2. 文件生成</div>
      <div class="row">
        <label for="fileContent">文件内容：</label>
        <textarea id="fileContent" v-model="fileContent"></textarea>
      </div>
      <div class="row">
        <label for="fileType">文件类型：</label>
        <select id="fileType" v-model="fileType">
          <option value="TXT">文本文件 (.txt)</option>
          <option value="MD">Markdown (.md)</option>
          <option value="JSON">JSON (.json)</option>
          <option value="CSV">CSV (.csv)</option>
          <option value="XML">XML (.xml)</option>
        </select>
      </div>
      <div class="row">
        <label for="fileName">文件名：</label>
        <input type="text" id="fileName" v-model="fileName" placeholder="可选，留空则自动生成" />
      </div>
      <div class="row">
        <label></label>
        <div>
          <button :disabled="!connected" @click="generateFile">生成并下载文件</button>
          <button :disabled="!fileData" @click="download">手动下载</button>
        </div>
      </div>
    </div>
    
    <div class="section">
      <div class="section-title">3. 下载进度</div>
      <div class="progress-container">
        <div class="progress-bar" :style="{ width: progressPercent + '%' }">
          {{ progressPercent }}% - {{ progressMessage }}
        </div>
      </div>
    </div>
    
    <div class="section">
      <div class="section-title">4. 状态日志</div>
      <div class="status" ref="statusRef">
        <div v-for="(log, index) in logs" :key="index" :class="['log', log.type]">
          [{{ log.time }}] {{ log.message }}
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch, nextTick } from 'vue';
import SseFileDownloaderClass from './sse-file-downloader.js';

// 响应式状态
const userId = ref(1);
const baseUrl = ref('/api/sse1');
const apiPrefix = ref(''); // 可以设置为 '/dev-api' 或其他前缀
const fileContent = ref(`这是一个测试文件内容。
可以包含多行文本，支持中文。

下面是一些 Markdown 示例：
# 标题1
## 标题2
- 列表项1
- 列表项2

**粗体** *斜体* [链接](http://example.com)`);
const fileType = ref('MD');
const fileName = ref('');
const connected = ref(false);
const logs = ref([]);
const progressPercent = ref(0);
const progressMessage = ref('未连接');
const statusRef = ref(null);
const fileData = ref(null);

// 下载器实例
let downloader = null;

// 获取完整的API URL
const getFullUrl = (path) => {
  const prefix = apiPrefix.value || '';
  const base = baseUrl.value.startsWith('/') 
    ? baseUrl.value 
    : '/' + baseUrl.value;
  return `${prefix}${base}`;
};

// 添加日志
const addLog = (message, type = 'info') => {
  const now = new Date();
  const timeStr = now.toLocaleTimeString();
  logs.value.push({
    time: timeStr,
    message,
    type
  });
  
  // 下一个tick时滚动到底部
  nextTick(() => {
    if (statusRef.value) {
      statusRef.value.scrollTop = statusRef.value.scrollHeight;
    }
  });
};

// 更新进度条
const updateProgress = (percent, message) => {
  progressPercent.value = percent;
  progressMessage.value = message || `${percent}%`;
};

// 格式化文件大小
const formatFileSize = (bytes) => {
  if (bytes < 1024) return bytes + ' B';
  if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(2) + ' KB';
  return (bytes / (1024 * 1024)).toFixed(2) + ' MB';
};

// 事件处理函数
const handleConnected = (data) => {
  addLog(`连接已建立: 用户ID ${data.userId}`, 'success');
};

const handleDisconnected = (data) => {
  addLog(`连接已断开: 用户ID ${data.userId}`, 'info');
  connected.value = false;
};

const handleFileInfo = (data) => {
  addLog(`接收到文件信息: ${data.fileName}, 大小: ${formatFileSize(data.fileSize)}`, 'info');
  updateProgress(25, '接收文件信息');
};

const handleFileData = (data) => {
  addLog(`接收到文件数据: ${data.fileName}, 数据大小: ${formatFileSize(data.data.length)}`, 'info');
  updateProgress(75, '接收文件数据');
  fileData.value = data;
};

const handleDownloadReady = (data) => {
  addLog(`文件下载就绪: ${data.fileName}, 类型: ${data.fileType}`, 'success');
  updateProgress(100, '下载就绪');
};

const handleError = (data) => {
  addLog(`发生错误: ${data.message}`, 'error');
};

const handleProgress = (data) => {
  if (data.progress !== undefined) {
    updateProgress(data.progress, data.message);
  }
  addLog(`进度更新: ${data.message} (${data.progress || 0}%)`, 'info');
};

// 连接到服务器
const connect = async () => {
  if (!userId.value || userId.value < 1) {
    addLog('请输入有效的用户ID', 'error');
    return;
  }

  const fullUrl = getFullUrl();
  addLog(`正在建立与用户ID ${userId.value} 的SSE连接，URL: ${fullUrl}...`, 'info');

  try {
    // 创建下载器实例
    downloader = new SseFileDownloaderClass({
      baseUrl: fullUrl,
      debug: true,
      autoDownload: true,
      onConnected: handleConnected,
      onDisconnected: handleDisconnected,
      onFileInfo: handleFileInfo,
      onFileData: handleFileData,
      onDownloadReady: handleDownloadReady,
      onError: handleError,
      onProgress: handleProgress
    });

    // 连接到服务器
    const result = await downloader.connect(userId.value);
    addLog(`连接成功: 用户ID ${result.userId}`, 'success');
    connected.value = true;
  } catch (error) {
    addLog(`连接失败: ${error.message}`, 'error');
  }
};

// 断开连接
const disconnect = () => {
  if (!downloader) {
    addLog('没有活跃的连接', 'warning');
    return;
  }

  addLog('正在断开SSE连接...', 'info');
  
  const result = downloader.disconnect();
  if (result) {
    addLog('已断开连接', 'success');
    connected.value = false;
    updateProgress(0, '未连接');
  } else {
    addLog('断开连接失败', 'error');
  }
};

// 检查连接状态
const checkStatus = async () => {
  if (!downloader || !downloader.userId) {
    addLog('没有设置用户ID，请先建立连接', 'warning');
    return;
  }

  addLog('正在检查连接状态...', 'info');

  try {
    const result = await downloader.checkStatus();
    addLog(`连接状态: ${result.connected ? '已连接' : '未连接'}`, 'info');
    connected.value = result.connected;
  } catch (error) {
    addLog(`检查状态失败: ${error.message}`, 'error');
  }
};

// 生成文件
const generateFile = async () => {
  if (!downloader || !connected.value) {
    addLog('未连接到SSE服务器，请先建立连接', 'error');
    return;
  }

  if (!fileContent.value) {
    addLog('请输入文件内容', 'error');
    return;
  }

  addLog(`正在请求生成${fileType.value}文件...`, 'info');
  updateProgress(0, '准备中');

  try {
    const result = await downloader.generateFile(fileContent.value, fileType.value, fileName.value);
    addLog(`文件生成请求已发送: ${result.message}`, 'success');
  } catch (error) {
    addLog(`生成文件请求失败: ${error.message}`, 'error');
  }
};

// 手动下载
const download = () => {
  if (!downloader) {
    addLog('未初始化下载器，请先建立连接', 'error');
    return;
  }

  const result = downloader.download();
  if (result) {
    addLog('文件下载已启动', 'success');
  } else {
    addLog('没有可下载的文件数据', 'warning');
  }
};

// 组件挂载时
onMounted(() => {
  addLog('组件已加载，请设置用户ID并建立连接', 'info');
});
</script>

<style scoped>
.container {
  max-width: 800px;
  margin: 0 auto;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  padding: 20px;
  font-family: 'Microsoft YaHei', Arial, sans-serif;
}

h1 {
  color: #3f51b5;
  text-align: center;
  margin-bottom: 30px;
}

.section {
  margin-bottom: 25px;
  padding-bottom: 20px;
  border-bottom: 1px solid #eee;
}

.section:last-child {
  border-bottom: none;
}

.section-title {
  font-size: 1.2rem;
  font-weight: bold;
  margin-bottom: 15px;
  color: #1976d2;
}

.row {
  display: flex;
  margin-bottom: 10px;
  align-items: center;
}

label {
  width: 120px;
  font-weight: bold;
}

input, select, textarea {
  flex: 1;
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-family: inherit;
}

textarea {
  min-height: 120px;
  resize: vertical;
}

button {
  background-color: #4caf50;
  color: white;
  border: none;
  padding: 10px 15px;
  margin: 5px;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s;
}

button:hover {
  background-color: #388e3c;
}

button:disabled {
  background-color: #9e9e9e;
  cursor: not-allowed;
}

.status {
  margin-top: 20px;
  padding: 15px;
  background-color: #f5f5f5;
  border-radius: 4px;
  font-family: monospace;
  max-height: 200px;
  overflow-y: auto;
}

.log {
  margin: 5px 0;
}

.log.info {
  color: #2196f3;
}

.log.success {
  color: #4caf50;
}

.log.error {
  color: #f44336;
}

.log.warning {
  color: #ff9800;
}

.progress-container {
  margin-top: 20px;
  background-color: #f5f5f5;
  border-radius: 4px;
  height: 25px;
}

.progress-bar {
  height: 100%;
  width: 0;
  background-color: #4caf50;
  border-radius: 4px;
  transition: width 0.3s ease;
  text-align: center;
  line-height: 25px;
  color: white;
  font-size: 14px;
}
</style> 