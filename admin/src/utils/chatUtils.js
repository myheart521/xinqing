import { ElMessage } from 'element-plus';
import { getChinaTime } from './timeUtils.js';
import {
  sendAIChatMessage,
  sendAdminAIAgentMessage,
  sendAdminMCPAIAgentMessage
} from '@/api/ai/chat.js';

/**
 * 检测文本中是否包含URL
 * @param {string} text 要检测的文本
 * @returns {boolean} 是否包含URL
 */
export function detectUrl(text) {
  const urlRegex = /(https?:\/\/[^\s]+)/g;
  return urlRegex.test(text);
}

/**
 * 处理流式消息响应
 * @param {Response} response 服务器响应
 * @param {object} tempMessage 临时消息对象，用于显示流式内容
 * @param {Function} updateMessages 更新消息列表的函数
 * @param {Function} scrollToBottom 滚动到底部的函数
 * @returns {Promise<void>}
 */
export async function handleStreamResponse(response, tempMessage, updateMessages, scrollToBottom) {
  if (response && response.body && typeof response.body.getReader === 'function') {
    const reader = response.body.getReader();
    const decoder = new TextDecoder('utf-8');
    let aiResponse = '';

    const readStream = async () => {
      try {
        const { value, done } = await reader.read();
        if (done) {
          // 流结束时设置isTyping为false
          tempMessage.isTyping = false;
          tempMessage.isReadingWebpage = false;
          return;
        }

        // 解码流数据
        const chunk = decoder.decode(value, { stream: true });
        
        // 处理SSE格式 (data:前缀)
        const dataLines = chunk.split('\n');

        // 判断是否是第一个数据块
        const isFirstChunk = aiResponse === '正在读取网页...' || aiResponse === '';

        // 完整的处理后内容
        let processedContent = '';

        // 逐行处理data:前缀的内容
        for (const line of dataLines) {
          if (line.startsWith('data:')) {
            // 去除data:前缀，保留实际内容
            let content = line.substring(5);

            // 特殊字符处理
            if (content === '-') {
              content = '\n';
            }

            processedContent += content;
          } else if (line.trim() !== '') {  // 非空行但不是以data:开头
            processedContent += line;
          }
        }

        // 只在有实际内容时更新
        if (processedContent) {
          // 如果是第一个内容且之前是"正在读取网页"，则替换而不是追加
          if (isFirstChunk && tempMessage.isReadingWebpage) {
            aiResponse = processedContent;
          } else {
            aiResponse += processedContent;
          }

          // 更新消息内容
          tempMessage.content = aiResponse;
          tempMessage.isReadingWebpage = false;
          updateMessages();

          // 滚动到底部
          scrollToBottom();
        }

        // 继续读取流
        readStream();
      } catch (error) {
        console.error('读取流数据失败:', error);
        // 出错时也要停止光标闪烁和网页读取状态
        tempMessage.isTyping = false;
        tempMessage.isReadingWebpage = false;
        updateMessages();
      }
    };

    await readStream();
  } else {
    // 非流式响应处理
    console.error('无法获取流式响应，尝试作为普通响应处理');
    tempMessage.isTyping = false;

    try {
      // 尝试获取文本响应
      const text = await response.text();
      tempMessage.content = text || '未获取到有效回复';
    } catch (error) {
      tempMessage.content = '处理响应时出错: ' + error.message;
    }
    
    updateMessages();
  }
}

/**
 * 发送消息并处理响应
 * @param {string} message 消息内容
 * @param {string} mode 当前模式
 * @param {string} memoryId 会话ID
 * @param {Array} messages 消息列表
 * @param {Function} updateMessages 更新消息的函数
 * @param {Function} scrollToBottom 滚动到底部的函数
 * @returns {Promise<object>} 临时消息对象
 */
export async function sendChatMessage(message, mode, memoryId, messages, updateMessages, scrollToBottom) {
  // 检测用户输入是否包含URL
  const hasUrl = detectUrl(message);

  // 添加用户消息
  messages.push({
    role: 'user',
    content: message,
    type: 'text',
    time: getChinaTime()
  });

  // 添加临时的AI回复消息
  const tempMessage = {
    role: 'assistant',
    content: hasUrl ? '正在读取网页...' : '',
    isTyping: true,
    type: 'text', 
    time: getChinaTime(),
    isReadingWebpage: hasUrl 
  };
  messages.push(tempMessage);
  updateMessages();

  // 滚动到底部
  scrollToBottom();

  try {
    let response;
    if (mode === 'webSearch') {
      // 联网搜索模式
      response = await sendAIChatMessage(message, memoryId);
    } else if (mode === 'adminAgent') {
      // 管理端智能体入口
      response = await sendAdminAIAgentMessage(message, memoryId);
    } else if (mode === 'adminMCP') {
      // 管理端MCP智能体入口
      response = await sendAdminMCPAIAgentMessage(message, memoryId);
    }

    // 处理响应
    await handleStreamResponse(response, tempMessage, updateMessages, scrollToBottom);
    
    return tempMessage;
  } catch (error) {
    console.error('发送消息失败:', error);
    tempMessage.isTyping = false;
    tempMessage.content = '抱歉，回复失败，请稍后再试。错误详情：' + (error.message || error);
    updateMessages();
    ElMessage.error('消息发送失败，请稍后再试');
    
    return tempMessage;
  }
}