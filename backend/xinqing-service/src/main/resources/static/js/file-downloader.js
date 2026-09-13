/**
 * SSE文件下载处理器
 * 
 * 使用方法:
 * 1. 引入此脚本
 * 2. 创建连接: const downloader = new FileDownloader(userId);
 * 3. 开始使用
 */
class FileDownloader {
    constructor(userId) {
        this.userId = userId;
        this.eventSource = null;
        this.connected = false;
        this.fileData = null;
        this.fileName = null;
        this.fileInfo = null;
        this.onFileReady = null;
        this.onProgress = null;
        this.onError = null;
        this.onConnect = null;
    }

    /**
     * 连接到SSE服务
     * @param {boolean} useMvcStyle 是否使用MVC风格连接 (默认不使用)
     * @returns {Promise} 连接成功的Promise
     */
    connect(useMvcStyle = false) {
        return new Promise((resolve, reject) => {
            try {
                // 关闭可能存在的连接
                this.disconnect();
                
                // 建立新连接
                const endpoint = useMvcStyle 
                    ? `/sse/connect-mvc/${this.userId}`
                    : `/sse/connect/${this.userId}`;
                
                this.eventSource = new EventSource(endpoint);
                
                // 连接事件
                this.eventSource.addEventListener('CONNECT', (event) => {
                    console.log('连接已建立', event.data);
                    this.connected = true;
                    if (this.onConnect) this.onConnect();
                    resolve();
                });
                
                // 文件信息事件
                this.eventSource.addEventListener('FILE_INFO', (event) => {
                    try {
                        this.fileInfo = JSON.parse(event.data);
                        console.log('接收到文件信息', this.fileInfo);
                    } catch (e) {
                        console.error('解析文件信息失败', e);
                    }
                });
                
                // 各种文件类型事件
                ['TEXT', 'MARKDOWN', 'EXCEL', 'WORD'].forEach(type => {
                    this.eventSource.addEventListener(type, (event) => {
                        this.fileName = event.data;
                        console.log(`接收到${type}文件`, this.fileName);
                    });
                });
                
                // 文件数据 (Base64编码)
                this.eventSource.addEventListener('FILE_DATA_BASE64', (event) => {
                    this.fileData = event.data;
                    console.log('接收到Base64编码的文件数据', this.fileData.substring(0, 50) + '...');
                    if (this.onProgress) this.onProgress(100);
                });
                
                // 下载准备就绪
                this.eventSource.addEventListener('DOWNLOAD_READY', (event) => {
                    console.log('文件下载准备就绪', event.data);
                    if (this.fileData && this.fileName && this.fileInfo) {
                        if (this.onFileReady) this.onFileReady({
                            fileName: this.fileName,
                            fileInfo: this.fileInfo,
                            data: this.fileData
                        });
                        
                        // 自动下载
                        this.downloadFile();
                    }
                });
                
                // 错误处理
                this.eventSource.addEventListener('ERROR', (event) => {
                    const errorMsg = event.data;
                    console.error('服务端错误', errorMsg);
                    if (this.onError) this.onError(errorMsg);
                });
                
                this.eventSource.onerror = (error) => {
                    console.error('SSE连接错误', error);
                    this.connected = false;
                    reject(error);
                };
                
            } catch (error) {
                console.error('创建SSE连接失败', error);
                reject(error);
            }
        });
    }
    
    /**
     * 断开连接
     */
    disconnect() {
        if (this.eventSource) {
            this.eventSource.close();
            this.eventSource = null;
            this.connected = false;
            console.log('连接已断开');
        }
    }
    
    /**
     * 下载文件
     * @returns {boolean} 是否开始下载
     */
    downloadFile() {
        if (!this.fileData || !this.fileName || !this.fileInfo) {
            console.error('文件数据不完整，无法下载');
            return false;
        }
        
        try {
            // 从Base64创建Blob
            const byteCharacters = atob(this.fileData);
            const byteNumbers = new Array(byteCharacters.length);
            for (let i = 0; i < byteCharacters.length; i++) {
                byteNumbers[i] = byteCharacters.charCodeAt(i);
            }
            const byteArray = new Uint8Array(byteNumbers);
            const blob = new Blob([byteArray], { type: this.fileInfo.mimeType });
            
            // 创建下载链接
            const url = URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.href = url;
            a.download = this.fileName;
            
            // 触发下载
            document.body.appendChild(a);
            a.click();
            
            // 清理
            setTimeout(() => {
                document.body.removeChild(a);
                URL.revokeObjectURL(url);
            }, 100);
            
            return true;
        } catch (error) {
            console.error('下载文件失败', error);
            return false;
        }
    }
    
    /**
     * 请求生成文件
     * @param {string} content 文件内容
     * @param {string} fileType 文件类型 (TXT, MD, EXCEL, WORD)
     */
    async generateFile(content, fileType) {
        if (!this.connected) {
            await this.connect();
        }
        
        // 重置状态
        this.fileData = null;
        this.fileName = null;
        this.fileInfo = null;
        
        // 发送请求
        try {
            const formData = new FormData();
            formData.append('content', content);
            formData.append('type', fileType);
            formData.append('userId', this.userId);
            
            const response = await fetch('/sse/generate', {
                method: 'POST',
                body: formData
            });
            
            if (!response.ok) {
                throw new Error(`请求失败 ${response.status}: ${response.statusText}`);
            }
            
            const result = await response.json();
            console.log('文件生成请求已发送', result);
            return result;
        } catch (error) {
            console.error('文件生成请求失败', error);
            throw error;
        }
    }
}

// 使用示例:
// const downloader = new FileDownloader(24);
// downloader.connect().then(() => {
//     downloader.generateFile("# 测试文档\n\n这是一个测试文档", "MD");
// }); 