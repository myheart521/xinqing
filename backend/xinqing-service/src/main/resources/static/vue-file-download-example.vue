<template>
  <div class="file-download-container">
    <h2>文件下载示例</h2>
    
    <!-- 连接状态 -->
    <div class="status-bar" :class="connectionClass">
      {{ status }}
    </div>
    
    <!-- 文件内容输入 -->
    <div class="form-group">
      <label for="content">文件内容:</label>
      <textarea 
        id="content" 
        v-model="content" 
        class="content-area"
        placeholder="输入要保存的文件内容..."
      ></textarea>
    </div>
    
    <!-- 操作按钮 -->
    <div class="button-group">
      <button 
        @click="connect" 
        :disabled="isConnected" 
        class="connect-btn"
      >连接服务器</button>
      
      <button 
        @click="disconnect" 
        :disabled="!isConnected" 
        class="disconnect-btn"
      >断开连接</button>
    </div>
    
    <!-- 文件类型选择 -->
    <div class="file-types">
      <button 
        v-for="type in fileTypes" 
        :key="type.value" 
        @click="generateFile(type.value)" 
        :disabled="!isConnected || !content"
        class="file-type-btn"
      >
        {{ type.label }}
      </button>
    </div>
    
    <!-- 进度显示 -->
    <div v-if="showProgress" class="progress-container">
      <div class="progress-label">接收进度：{{ downloader.progress }}</div>
    </div>
  </div>
</template>

<script>
import { ref, reactive, computed, onMounted, onUnmounted } from 'vue';
import VueFileDownloader from './js/vue-file-downloader';

export default {
  name: 'FileDownloadExample',
  
  setup() {
    // 响应式状态
    const userId = ref(24); // 可根据实际情况修改
    const content = ref('# 标题\n\n这是一个测试文档。\n\n## 二级标题\n\n- 项目1\n- 项目2\n- 项目3');
    const status = ref('请先连接服务器');
    const downloader = reactive(new VueFileDownloader());
    const showProgress = ref(false);
    
    // 文件类型选项
    const fileTypes = [
      { label: '生成文本文件', value: 'TXT' },
      { label: '生成Markdown', value: 'MD' },
      { label: '生成Excel', value: 'EXCEL' },
      { label: '生成Word', value: 'WORD' },
    ];
    
    // 计算属性
    const isConnected = computed(() => downloader.connected);
    
    const connectionClass = computed(() => {
      if (downloader.connected) return 'connected';
      return 'disconnected';
    });
    
    // 方法
    const connect = async () => {
      try {
        status.value = '正在连接服务器...';
        await downloader.connect(userId.value);
        status.value = '已连接到服务器';
      } catch (error) {
        status.value = `连接失败: ${error.message}`;
      }
    };
    
    const disconnect = () => {
      downloader.disconnect();
      status.value = '已断开连接';
      showProgress.value = false;
    };
    
    const generateFile = async (fileType) => {
      if (!isConnected.value) {
        status.value = '请先连接服务器';
        return;
      }
      
      if (!content.value) {
        status.value = '请输入文件内容';
        return;
      }
      
      try {
        status.value = `正在生成${fileType}文件...`;
        showProgress.value = true;
        await downloader.generateFile(userId.value, content.value, fileType);
      } catch (error) {
        status.value = `生成文件失败: ${error.message}`;
        showProgress.value = false;
      }
    };
    
    // 生命周期钩子
    onMounted(() => {
      // 如果需要可以在这里自动连接
      // connect();
    });
    
    onUnmounted(() => {
      // 组件销毁时断开连接
      disconnect();
    });
    
    return {
      userId,
      content,
      status,
      downloader,
      showProgress,
      fileTypes,
      isConnected,
      connectionClass,
      connect,
      disconnect,
      generateFile
    };
  }
}
</script>

<style scoped>
.file-download-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
  font-family: Arial, sans-serif;
}

.status-bar {
  padding: 10px;
  margin-bottom: 20px;
  border-radius: 4px;
  text-align: center;
}

.connected {
  background-color: #d4edda;
  color: #155724;
}

.disconnected {
  background-color: #f8d7da;
  color: #721c24;
}

.form-group {
  margin-bottom: 20px;
}

label {
  display: block;
  margin-bottom: 5px;
  font-weight: bold;
}

.content-area {
  width: 100%;
  height: 200px;
  padding: 10px;
  box-sizing: border-box;
  border: 1px solid #ddd;
  border-radius: 4px;
  resize: vertical;
}

.button-group {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
}

button {
  padding: 10px 15px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-weight: bold;
}

button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.connect-btn {
  background-color: #4CAF50;
  color: white;
}

.disconnect-btn {
  background-color: #dc3545;
  color: white;
}

.file-types {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 10px;
  margin-bottom: 20px;
}

.file-type-btn {
  background-color: #007bff;
  color: white;
}

.progress-container {
  margin-top: 20px;
  padding: 10px;
  background-color: #e9ecef;
  border-radius: 4px;
}

.progress-label {
  text-align: center;
  font-weight: bold;
}
</style> 