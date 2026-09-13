/**
 * Vue3版本的文件下载工具，通过SSE连接接收和下载文件
 */
export default class VueFileDownloader {
  constructor() {
    this.eventSource = null;
    this.connected = false;
    this.fileData = null;
    this.fileName = null;
    this.fileInfo = null;
    this.chunks = [];
    this.progress = 0;
  }

  /**
   * 建立SSE连接
   * @param {number} userId 用户ID
   * @returns {Promise} 连接成功的Promise
   */
  connect(userId) {
    return new Promise((resolve, reject) => {
      try {
        console.log("正在建立SSE连接:", userId);
        
        // 关闭可能存在的连接
        this.disconnect();
        
        // 重置数据
        this.fileData = null;
        this.fileName = null;
        this.fileInfo = null;
        this.chunks = [];
        this.progress = 0;
        
        // 建立连接
        const url = `/dev-api/sse/connect/${userId}`;
        console.log("正在连接到SSE端点:", url);
        this.eventSource = new EventSource(url);
        
        // 连接打开事件
        this.eventSource.onopen = () => {
          console.log('SSE连接已打开');
        };
        
        // 连接成功事件
        this.eventSource.addEventListener('CONNECT', (event) => {
          console.log('SSE连接已建立:', event.data);
          this.connected = true;
          resolve(true);
        });
        
        // 文件信息事件
        this.eventSource.addEventListener('FILE_INFO', (event) => {
          try {
            console.log('接收到文件信息事件:', event.data);
            this.fileInfo = JSON.parse(event.data);
            console.log('解析后的文件信息:', this.fileInfo);
          } catch (e) {
            console.error('解析文件信息失败:', e);
          }
        });
        
        // 监听各种文件类型事件
        ['TEXT', 'MARKDOWN', 'MD', 'EXCEL', 'WORD'].forEach(type => {
          this.eventSource.addEventListener(type, (event) => {
            console.log(`接收到${type}文件名:`, event.data);
            this.fileName = event.data;
          });
        });
        
        // 文件数据块事件
        this.eventSource.addEventListener('STREAM_CHUNK', (event) => {
          try {
            console.log('接收到数据块事件');
            if (typeof event.data === 'string') {
              this.chunks.push(event.data);
            } else {
              this.chunks.push(event.data);
            }
            this.progress = this.chunks.length;
            console.log(`接收到数据块 #${this.progress}`);
          } catch (e) {
            console.error('处理数据块失败:', e);
          }
        });
        
        // Base64编码的完整文件数据
        this.eventSource.addEventListener('FILE_DATA_BASE64', (event) => {
          console.log('接收到Base64编码的文件数据');
          this.fileData = event.data;
          this.progress = 100;
          // 立即触发下载
          this.downloadFile();
        });
        
        // 下载准备就绪事件
        this.eventSource.addEventListener('DOWNLOAD_READY', (event) => {
          console.log('文件下载准备就绪:', event.data);
          // 如果还没有下载，则触发下载
          if (!this.fileData && this.chunks.length > 0) {
            this.downloadFromChunks();
          }
        });
        
        // 完成事件
        ['TEXT_COMPLETE', 'MARKDOWN_COMPLETE', 'MD_COMPLETE', 'EXCEL_COMPLETE', 'WORD_COMPLETE'].forEach(type => {
          this.eventSource.addEventListener(type, () => {
            console.log(`${type}事件: 传输完成`);
            if (this.chunks.length > 0 && !this.fileData) {
              this.downloadFromChunks();
            }
          });
        });
        
        // 添加通用消息处理器
        this.eventSource.onmessage = (event) => {
          console.log('收到SSE消息:', event);
          console.log('事件类型:', event.type);
          console.log('事件数据:', event.data);
          
          // 根据事件类型处理数据
          switch(event.type) {
            case 'FILE_INFO':
              try {
                this.fileInfo = JSON.parse(event.data);
                console.log('解析后的文件信息:', this.fileInfo);
              } catch (e) {
                console.error('解析文件信息失败:', e);
              }
              break;
              
            case 'FILE_DATA_BASE64':
              console.log('接收到Base64编码的文件数据');
              this.fileData = event.data;
              this.progress = 100;
              this.downloadFile();
              break;
              
            case 'DOWNLOAD_READY':
              console.log('文件下载准备就绪:', event.data);
              if (!this.fileData && this.chunks.length > 0) {
                this.downloadFromChunks();
              }
              break;
              
            case 'MARKDOWN':
            case 'MD':
              console.log(`接收到${event.type}文件名:`, event.data);
              this.fileName = event.data;
              break;
          }
        };
        
        // 错误处理
        this.eventSource.addEventListener('ERROR', (event) => {
          const errorMsg = event.data;
          console.error('服务端错误:', errorMsg);
          reject(new Error(errorMsg));
        });
        
        // 连接错误
        this.eventSource.onerror = (error) => {
          console.error('SSE连接错误:', error);
          this.connected = false;
          reject(error);
        };
        
      } catch (error) {
        console.error('建立SSE连接失败:', error);
        reject(error);
      }
    });
  }
  
  /**
   * 断开SSE连接
   */
  disconnect() {
    if (this.eventSource) {
      console.log('正在断开SSE连接');
      this.eventSource.close();
      this.eventSource = null;
      this.connected = false;
    }
  }
  
  /**
   * 从数据块下载文件
   */
  downloadFromChunks() {
    if (this.chunks.length === 0 || !this.fileName || !this.fileInfo) {
      console.error('缺少必要的文件信息，无法下载');
      return;
    }
    
    try {
      console.log('开始从数据块下载文件:', this.fileName);
      const blob = new Blob(this.chunks, { type: this.fileInfo.mimeType });
      const url = URL.createObjectURL(blob);
      
      const a = document.createElement('a');
      a.href = url;
      a.download = this.fileName;
      document.body.appendChild(a);
      a.click();
      document.body.removeChild(a);
      URL.revokeObjectURL(url);
      
      console.log('文件下载完成');
    } catch (error) {
      console.error('文件下载失败:', error);
    }
  }
  
  /**
   * 从Base64数据下载文件
   */
  downloadFile() {
    if (!this.fileData || !this.fileName || !this.fileInfo) {
      console.error('缺少必要的文件信息，无法下载');
      return;
    }
    
    try {
      console.log('开始下载文件:', this.fileName);
      const binaryData = atob(this.fileData);
      const bytes = new Uint8Array(binaryData.length);
      for (let i = 0; i < binaryData.length; i++) {
        bytes[i] = binaryData.charCodeAt(i);
      }
      
      const blob = new Blob([bytes], { type: this.fileInfo.mimeType });
      const url = URL.createObjectURL(blob);
      
      const a = document.createElement('a');
      a.href = url;
      a.download = this.fileName;
      document.body.appendChild(a);
      a.click();
      document.body.removeChild(a);
      URL.revokeObjectURL(url);
      
      console.log('文件下载完成');
    } catch (error) {
      console.error('文件下载失败:', error);
    }
  }
  
  /**
   * 获取MIME类型
   * @returns {string} MIME类型
   */
  getMimeType() {
    // 如果接收到了文件信息，从中获取MIME类型
    if (this.fileInfo && this.fileInfo.mimeType) {
      return this.fileInfo.mimeType;
    }
    
    // 根据文件名判断MIME类型
    if (this.fileName) {
      const lowerName = this.fileName.toLowerCase();
      if (lowerName.endsWith('.md')) return 'text/markdown';
      if (lowerName.endsWith('.txt')) return 'text/plain';
      if (lowerName.endsWith('.docx')) return 'application/vnd.openxmlformats-officedocument.wordprocessingml.document';
      if (lowerName.endsWith('.xlsx')) return 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet';
    }
    
    // 默认返回二进制类型
    return 'application/octet-stream';
  }
  
  /**
   * 获取文件扩展名
   * @returns {string} 文件扩展名
   */
  getExtension() {
    // 如果接收到了文件信息，从中获取扩展名
    if (this.fileInfo && this.fileInfo.extension) {
      return this.fileInfo.extension;
    }
    
    // 根据文件名获取扩展名
    if (this.fileName && this.fileName.includes('.')) {
      return this.fileName.split('.').pop();
    }
    
    // 默认返回二进制扩展名
    return 'bin';
  }
  
  /**
   * 请求生成文件
   * @param {number} userId 用户ID
   * @param {string} content 文件内容
   * @param {string} fileType 文件类型 (TXT, MD, EXCEL, WORD)
   * @returns {Promise} 请求结果
   */
  async generateFile(userId, content, fileType) {
    if (!this.connected) {
      console.log('未建立连接，正在尝试连接...');
      await this.connect(userId);
    }
    
    // 重置数据
    this.fileData = null;
    this.fileName = null;
    this.fileInfo = null;
    this.chunks = [];
    this.progress = 0;
    
    // 发送请求
    try {
      console.log('准备发送文件生成请求:', { userId, fileType });
      const formData = new FormData();
      formData.append('content', content);
      formData.append('type', fileType);
      formData.append('userId', userId);
      
      const response = await fetch('/dev-api/sse/generate', {
        method: 'POST',
        body: formData
      });
      
      if (!response.ok) {
        throw new Error(`请求失败 ${response.status}: ${response.statusText}`);
      }
      
      const result = await response.json();
      console.log('文件生成请求已发送，响应:', result);
      return result;
    } catch (error) {
      console.error('文件生成请求失败:', error);
      throw error;
    }
  }
} 