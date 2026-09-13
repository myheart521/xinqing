import {ref} from "vue";
import {requestUrl} from "@/utils/URL.js";

/**
 * SSE文件下载器
 * 用于通过SSE连接接收和下载服务器发送的文件
 */
class SseFileDownloader {
    constructor(options = {}) {
        // 配置选项
        this.baseUrl = requestUrl+'/api/sse1';
        this.autoConnect = options.autoConnect || false;
        this.autoDownload = options.autoDownload !== false; // 默认为true
        this.debug = options.debug || false;

        // 事件处理函数
        this.onConnected = options.onConnected || null;
        this.onDisconnected = options.onDisconnected || null;
        this.onFileInfo = options.onFileInfo || null;
        this.onFileData = options.onFileData || null;
        this.onDownloadReady = options.onDownloadReady || null;
        this.onError = options.onError || null;
        this.onProgress = options.onProgress || null;

        // 状态
        this.connected = false;
        this.userId = null;
        this.eventSource = null;
        this.currentFileInfo = null;
        this.fileData = null;

        // 如果配置了自动连接，则立即连接
        if (this.autoConnect && options.userId) {
            this.connect(options.userId);
        }
    }

    /**
     * 连接到SSE服务器
     * @param {number} userId - 用户ID
     * @returns {Promise} - 连接结果的Promise
     */
    connect(userId) {
        this.userId = userId;

        return new Promise((resolve, reject) => {
            // 关闭现有连接
            if (this.eventSource) {
                this.disconnect();
            }
            try {
                const url = `${this.baseUrl}/connect/${userId}`;
                this.log(`正在连接到SSE服务器: ${url}`);
                this.eventSource = new EventSource(url);

                // 连接打开事件
                this.eventSource.onopen = (event) => {
                    this.log('SSE连接已打开');
                };

                // 注册事件监听器
                this.setupEventListeners();
                // 监听CONNECT事件以确认连接建立
                const connectHandler = (event) => {
                    this.connected = true;
                    this.log(`连接成功: 用户ID ${userId}`);

                    // 调用连接回调
                    if (typeof this.onConnected === 'function') {
                        this.onConnected({
                            userId: this.userId,
                            connected: true,
                            timestamp: new Date()
                        });
                    }

                    // 解析Promise
                    resolve({
                        success: true,
                        userId: this.userId,
                        connected: true
                    });

                    // 移除临时监听器
                    this.eventSource.removeEventListener('CONNECT', connectHandler);
                };

                this.eventSource.addEventListener('CONNECT', connectHandler);

                // 错误处理
                this.eventSource.onerror = (error) => {
                    this.log('SSE连接错误', error);

                    // 连接失败时拒绝Promise
                    if (!this.connected) {
                        reject(new Error('连接SSE服务器失败'));

                        // 调用错误回调
                        if (typeof this.onError === 'function') {
                            this.onError({
                                type: 'CONNECTION_ERROR',
                                message: '连接SSE服务器失败',
                                error: error
                            });
                        }
                    }
                };

            } catch (error) {
                this.log('创建SSE连接时出错', error);
                reject(error);

                // 调用错误回调
                if (typeof this.onError === 'function') {
                    this.onError({
                        type: 'CONNECTION_FAILED',
                        message: '创建SSE连接失败',
                        error: error
                    });
                }
            }
        });
    }

    /**
     * 设置SSE事件监听器
     */
    setupEventListeners() {
        if (!this.eventSource) return;

        // 文件信息事件
        this.eventSource.addEventListener('FILE_INFO', (event) => {
            try {
                const fileInfo = JSON.parse(event.data);
                this.currentFileInfo = fileInfo;
                this.log('接收到文件信息', fileInfo);

                // 重置文件数据
                this.fileData = null;

                // 调用文件信息回调
                if (typeof this.onFileInfo === 'function') {
                    this.onFileInfo(fileInfo);
                }

                // 调用进度回调
                if (typeof this.onProgress === 'function') {
                    this.onProgress({
                        type: 'info',
                        message: '文件信息已接收',
                        progress: 0
                    });
                }
            } catch (error) {
                this.handleError('处理文件信息时出错', error);
            }
        });

        // 文件数据事件
        this.eventSource.addEventListener('FILE_DATA', (event) => {
            try {
                const data = JSON.parse(event.data);
                this.fileData = data;
                this.log('接收到文件数据', {fileName: data.fileName, dataSize: data.data.length});

                // 调用文件数据回调
                if (typeof this.onFileData === 'function') {
                    this.onFileData(data);
                }

                // 调用进度回调
                if (typeof this.onProgress === 'function') {
                    this.onProgress({
                        type: 'data',
                        message: '文件数据已接收',
                        progress: 75
                    });
                }

                // 如果启用了自动下载，则直接下载文件
                if (this.autoDownload && data.data) {
                    this.downloadFile(data.fileName, data.data, data.mimeType);
                }
            } catch (error) {
                this.handleError('处理文件数据时出错', error);
            }
        });

        // 下载就绪事件
        this.eventSource.addEventListener('DOWNLOAD_READY', (event) => {
            try {
                const data = JSON.parse(event.data);
                this.log('文件下载就绪', data);

                // 调用下载就绪回调
                if (typeof this.onDownloadReady === 'function') {
                    this.onDownloadReady(data);
                }

                // 调用进度回调
                if (typeof this.onProgress === 'function') {
                    this.onProgress({
                        type: 'ready',
                        message: '文件下载就绪',
                        progress: 100
                    });
                }

                // 如果已接收文件数据但未自动下载，则提示手动下载
                if (!this.autoDownload && this.fileData) {
                    this.log('文件已就绪，可以手动下载');
                }
            } catch (error) {
                this.handleError('处理下载就绪事件时出错', error);
            }
        });

        // 错误事件
        this.eventSource.addEventListener('ERROR', (event) => {
            try {
                const error = JSON.parse(event.data);
                this.handleError('服务器发送的错误', error);
            } catch (error) {
                this.handleError('处理错误事件时出错', error);
            }
        });

        // 进度事件
        this.eventSource.addEventListener('PROGRESS', (event) => {
            try {
                const progress = JSON.parse(event.data);
                this.log('接收到进度更新', progress);

                // 调用进度回调
                if (typeof this.onProgress === 'function') {
                    this.onProgress(progress);
                }
            } catch (error) {
                this.handleError('处理进度事件时出错', error);
            }
        });

        // 处理一般消息事件
        this.eventSource.onmessage = (event) => {
            this.log('接收到一般消息', event.data);
        };
    }

    /**
     * 断开SSE连接
     */
    disconnect() {
        if (this.eventSource) {
            this.log('断开SSE连接');
            this.eventSource.close();
            this.eventSource = null;
            this.connected = false;

            // 调用断开连接回调
            if (typeof this.onDisconnected === 'function') {
                this.onDisconnected({
                    userId: this.userId,
                    connected: false,
                    timestamp: new Date()
                });
            }

            // 向服务器发送断开连接请求
            if (this.userId) {
                fetch(`${this.baseUrl}/disconnect/${this.userId}`, {
                    method: 'DELETE'
                }).then(response => response.json())
                    .then(data => this.log('服务器断开响应', data))
                    .catch(error => this.log('发送断开请求时出错', error));
            }

            return true;
        }
        return false;
    }

    /**
     * 请求生成并下载文件
     * @param {string} content - 文件内容
     * @param {string} fileType - 文件类型
     * @param {string} fileName - 文件名(可选)
     * @returns {Promise} - 请求结果的Promise
     */
    generateFile(content, fileType, fileName = '') {
        if (!this.connected || !this.userId) {
            return Promise.reject(new Error('未连接到SSE服务器'));
        }

        const formData = new FormData();
        formData.append('userId', this.userId);
        formData.append('content', content);
        formData.append('fileType', fileType);
        if (fileName) {
            formData.append('fileName', fileName);
        }

        this.log(`请求生成文件: 类型=${fileType}, 文件名=${fileName || '自动生成'}`);

        return fetch(`${this.baseUrl}/transfer`, {
            method: 'POST',
            body: formData
        })
            .then(response => response.json())
            .then(data => {
                this.log('文件生成请求响应', data);
                return data;
            })
            .catch(error => {
                this.handleError('请求生成文件时出错', error);
                throw error;
            });
    }

    /**
     * 下载已接收的文件
     * @param {string} fileName - 文件名
     * @param {string} base64Data - Base64编码的文件数据
     * @param {string} mimeType - 文件MIME类型
     */
    downloadFile(fileName, base64Data, mimeType) {
        try {
            // 解码Base64数据
            const byteCharacters = atob(base64Data);
            const byteArrays = [];

            for (let offset = 0; offset < byteCharacters.length; offset += 512) {
                const slice = byteCharacters.slice(offset, offset + 512);

                const byteNumbers = new Array(slice.length);
                for (let i = 0; i < slice.length; i++) {
                    byteNumbers[i] = slice.charCodeAt(i);
                }

                const byteArray = new Uint8Array(byteNumbers);
                byteArrays.push(byteArray);
            }

            // 创建Blob对象
            const blob = new Blob(byteArrays, {type: mimeType});

            // 创建下载链接
            const url = URL.createObjectURL(blob);
            const link = document.createElement('a');
            link.href = url;
            link.download = fileName;
            link.style.display = 'none';

            // 添加到文档并触发点击
            document.body.appendChild(link);
            link.click();

            // 清理
            setTimeout(() => {
                document.body.removeChild(link);
                URL.revokeObjectURL(url);
            }, 100);

            this.log(`文件下载已启动: ${fileName}`);
            return true;
        } catch (error) {
            this.handleError('下载文件时出错', error);
            return false;
        }
    }

    /**
     * 检查连接状态
     * @returns {Promise} - 连接状态的Promise
     */
    checkStatus() {
        if (!this.userId) {
            return Promise.reject(new Error('未设置用户ID'));
        }

        return fetch(`${this.baseUrl}/status/${this.userId}`)
            .then(response => response.json())
            .then(data => {
                this.connected = data.connected;
                return data;
            })
            .catch(error => {
                this.handleError('检查连接状态时出错', error);
                throw error;
            });
    }

    /**
     * 手动下载当前文件
     * @returns {boolean} - 是否成功启动下载
     */
    download() {
        if (!this.fileData || !this.fileData.data) {
            this.log('没有可下载的文件数据');
            return false;
        }

        return this.downloadFile(
            this.fileData.fileName,
            this.fileData.data,
            this.fileData.mimeType
        );
    }

    /**
     * 处理错误
     * @param {string} message - 错误消息
     * @param {Error|Object} error - 错误对象
     */
    handleError(message, error) {
        this.log(message, error);

        // 调用错误回调
        if (typeof this.onError === 'function') {
            this.onError({
                type: 'ERROR',
                message: message,
                error: error
            });
        }
    }

    /**
     * 记录日志
     * @param {string} message - 日志消息
     * @param {any} data - 附加数据
     */
    log(message, data) {
        if (this.debug) {
            if (data) {
                console.log(`[SseFileDownloader] ${message}:`, data);
            } else {
                console.log(`[SseFileDownloader] ${message}`);
            }
        }
    }
}

export default SseFileDownloader;

// 格式化文件大小
export const formatFileSize = (bytes) => {
    if (bytes < 1024) return bytes + ' B';
    if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(2) + ' KB';
    return (bytes / (1024 * 1024)).toFixed(2) + ' MB';
};

const baseUrl = '/dev-api/api/sse1';
const apiPrefix = '';
// 获取完整的API URL
export const getFullUrl = (path) => {
    const prefix = apiPrefix || '';
    const base = baseUrl.startsWith('/')
        ? baseUrl
        : '/' + baseUrl;
    return `${prefix}${base}`;
};

