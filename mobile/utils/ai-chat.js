import { requestTask } from '@/service/api/aiQwenController';

/**
 * 生成简单的唯一标识符
 * 兼容微信小程序环境
 */
function generateUniqueId() {
  return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
    const r = Math.random() * 16 | 0;
    const v = c === 'x' ? r : (r & 0x3 | 0x8);
    return v.toString(16);
  }) + '-' + Date.now();
}

/**
 * AI聊天工具 - 提供与AI对话的流式响应功能，支持会话记忆
 */
export default {
  
  // 当前会话的唯一标识，用于后端记忆对话内容
  memoryId: null,
  
  /**
   * 创建新的会话
   * @returns {string} 新的会话ID
   */
  createNewSession() {
    this.memoryId = generateUniqueId();
    return this.memoryId;
  },
  
  /**
   * 获取当前会话ID，如果不存在则创建
   * @returns {string} 会话ID
   */
  getSessionId() {
    if (!this.memoryId) {
      return this.createNewSession();
    }
    return this.memoryId;
  },
  
  /**
   * 发送消息并获取流式响应
   * @param {string} message - 用户发送的消息
   * @param {Array} chatHistory - 历史对话记录 (用于前端展示，后端通过memoryId维护记忆)
   * @param {Function} onStream - 流式响应回调，用于逐步更新UI
   * @param {Function} onComplete - 完成回调
   * @param {Function} onError - 错误回调
   * @param {boolean} isNewSession - 是否为新会话，如果是则重置会话ID
   */
  async sendMessage(message, chatHistory = [], onStream, onComplete, onError, isNewSession = false) {
    try {
      // 如果是新会话，重置会话ID
      if (isNewSession) {
        this.createNewSession();
      }
      
      // 请求参数 - 传递prompt和memoryId
      const params = {
        prompt: message,
        memoryId: this.getSessionId()
      };
      
      console.log('发送消息参数:', params);
      
      // 创建完整响应容器
      let fullResponse = '';
      
      // 创建Promise以便等待流式传输完成
      return new Promise((resolve, reject) => {
        // 使用requestTask实现流式响应
        const task = requestTask(
          params,
          // 数据块接收回调
          (chunk) => {
            console.log("收到数据块:", chunk);
            fullResponse += chunk;
            
            // 调用流式更新回调
            if (onStream && typeof onStream === 'function') {
              onStream(fullResponse);
            }
          },
          // 完成回调
          () => {
            console.log("流式响应完成");
            if (onComplete && typeof onComplete === 'function') {
              onComplete(fullResponse);
            }
            resolve(fullResponse);
          },
          // 错误回调
          (error) => {
            console.error("流式响应错误:", error);
            const errorMsg = error.errMsg || '网络错误，请稍后再试';
            if (onError && typeof onError === 'function') {
              onError(errorMsg);
            }
            reject(new Error(errorMsg));
          }
        );
      });
    } catch (error) {
      console.error('AI对话错误:', error);
      if (onError && typeof onError === 'function') {
        onError(error.message || '网络错误，请稍后再试');
      }
      return null;
    }
  }
}; 