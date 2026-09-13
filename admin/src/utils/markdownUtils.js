import { marked } from 'marked';

/**
 * 渲染Markdown内容
 * @param {string} content 原始文本内容
 * @returns {string} 处理后的HTML
 */
export function renderMarkdown(content) {
  if (!content) return '';
  
  try {
    // 尝试解析Markdown
    return marked.parse(content);
  } catch (error) {
    console.error('Markdown解析失败:', error);
    return content;
  }
}

/**
 * 格式化消息内容，处理URL链接
 * @param {string} content 消息内容
 * @returns {string} 处理后的内容
 */
export function formatMessageContent(content) {
  if (!content) return '';
  
  // 将URL转换为可点击的链接
  const urlRegex = /(https?:\/\/[^\s]+)/g;
  return content.replace(urlRegex, (url) => {
    return `<a href="${url}" target="_blank" rel="noopener noreferrer">${url}</a>`;
  });
}

/**
 * 判断内容是否是JSON
 * @param {string} str 需要判断的字符串
 * @returns {boolean} 是否是JSON
 */
export function isJsonString(str) {
  if (typeof str !== 'string') return false;
  if (!str.trim().startsWith('{') && !str.trim().startsWith('[')) return false;
  
  try {
    JSON.parse(str);
    return true;
  } catch (e) {
    return false;
  }
}

/**
 * 检测文本是否包含代码块
 * @param {string} text 要检测的文本
 * @returns {boolean} 是否包含代码块
 */
export function hasCodeBlock(text) {
  if (!text) return false;
  
  // 检测Markdown代码块语法 ```language code ```
  const codeBlockRegex = /```[\s\S]+?```/g;
  // 检测行内代码语法 `code`
  const inlineCodeRegex = /`[^`]+`/g;
  
  return codeBlockRegex.test(text) || inlineCodeRegex.test(text);
}