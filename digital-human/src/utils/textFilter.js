/**
 * 文本过滤工具
 * 用于清理发送给数字人的文本内容，移除各种杂乱字符和格式
 */

/**
 * 基础文本清理函数
 * @param {string} text - 原始文本
 * @returns {string} - 清理后的文本
 */
export const basicTextClean = (text) => {
  if (!text || typeof text !== 'string') {
    return ''
  }

  return text
    // 移除HTML标签
    .replace(/<[^>]*>/g, '')
    // 移除Markdown标记
    .replace(/#+\s*/g, '') // 标题标记 # ## ###
    .replace(/\*\*(.*?)\*\*/g, '$1') // 粗体 **text**
    .replace(/\*(.*?)\*/g, '$1') // 斜体 *text*
    .replace(/__(.*?)__/g, '$1') // 下划线粗体 __text__
    .replace(/_(.*?)_/g, '$1') // 下划线斜体 _text_
    .replace(/~~(.*?)~~/g, '$1') // 删除线 ~~text~~
    .replace(/`(.*?)`/g, '$1') // 行内代码 `code`
    .replace(/```[\s\S]*?```/g, '') // 代码块
    .replace(/\[(.*?)\]\(.*?\)/g, '$1') // 链接 [text](url)
    .replace(/!\[.*?\]\(.*?\)/g, '') // 图片 ![alt](url)
    // 移除特殊符号
    .replace(/[#@$%^&*()+=\[\]{}|\\:";'<>?,.\/~`]/g, ' ')
    // 移除多余的空白字符
    .replace(/\s+/g, ' ')
    // 移除首尾空白
    .trim()
}

/**
 * 高级文本清理函数
 * @param {string} text - 原始文本
 * @returns {string} - 清理后的文本
 */
export const advancedTextClean = (text) => {
  if (!text || typeof text !== 'string') {
    return ''
  }

  let cleanText = text

  // 移除URL链接
  cleanText = cleanText.replace(/https?:\/\/[^\s]+/g, '')
  cleanText = cleanText.replace(/www\.[^\s]+/g, '')
  
  // 移除邮箱地址
  cleanText = cleanText.replace(/[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}/g, '')
  
  // 移除电话号码
  cleanText = cleanText.replace(/\d{3}-\d{3}-\d{4}/g, '')
  cleanText = cleanText.replace(/\d{3}\s\d{3}\s\d{4}/g, '')
  cleanText = cleanText.replace(/\(\d{3}\)\s\d{3}-\d{4}/g, '')
  
  // 移除过长的数字串（可能是ID或其他标识符）
  cleanText = cleanText.replace(/\d{6,}/g, '')
  
  // 移除重复的标点符号
  cleanText = cleanText.replace(/[。！？]{2,}/g, '。')
  cleanText = cleanText.replace(/[,，]{2,}/g, '，')
  cleanText = cleanText.replace(/[;；]{2,}/g, '；')
  
  // 移除特殊Unicode字符
  cleanText = cleanText.replace(/[\u200B-\u200D\uFEFF]/g, '') // 零宽字符
  cleanText = cleanText.replace(/[\u2000-\u206F]/g, ' ') // 各种空格字符
  
  // 移除emoji表情（可选，根据需要启用）
  // cleanText = cleanText.replace(/[\u{1F600}-\u{1F64F}]|[\u{1F300}-\u{1F5FF}]|[\u{1F680}-\u{1F6FF}]|[\u{1F1E0}-\u{1F1FF}]|[\u{2600}-\u{26FF}]|[\u{2700}-\u{27BF}]/gu, '')
  
  // 应用基础清理
  cleanText = basicTextClean(cleanText)
  
  return cleanText
}

/**
 * 智能文本清理函数（推荐使用）
 * @param {string} text - 原始文本
 * @param {Object} options - 清理选项
 * @param {boolean} options.removeEmoji - 是否移除emoji，默认false
 * @param {boolean} options.removeNumbers - 是否移除数字，默认false
 * @param {boolean} options.removeEnglish - 是否移除英文，默认false
 * @param {boolean} options.maxLength - 最大长度限制，默认500
 * @returns {string} - 清理后的文本
 */
export const smartTextClean = (text, options = {}) => {
  const {
    removeEmoji = false,
    removeNumbers = false,
    removeEnglish = false,
    maxLength = 500
  } = options

  if (!text || typeof text !== 'string') {
    return ''
  }

  let cleanText = text

  // 应用高级清理
  cleanText = advancedTextClean(cleanText)

  // 可选：移除emoji
  if (removeEmoji) {
    cleanText = cleanText.replace(/[\u{1F600}-\u{1F64F}]|[\u{1F300}-\u{1F5FF}]|[\u{1F680}-\u{1F6FF}]|[\u{1F1E0}-\u{1F1FF}]|[\u{2600}-\u{26FF}]|[\u{2700}-\u{27BF}]/gu, '')
  }

  // 可选：移除数字
  if (removeNumbers) {
    cleanText = cleanText.replace(/\d+/g, '')
  }

  // 可选：移除英文
  if (removeEnglish) {
    cleanText = cleanText.replace(/[a-zA-Z]+/g, '')
  }

  // 清理多余空格
  cleanText = cleanText.replace(/\s+/g, ' ').trim()

  // 长度限制
  if (cleanText.length > maxLength) {
    cleanText = cleanText.substring(0, maxLength) + '...'
  }

  return cleanText
}

/**
 * 专门用于数字人的文本过滤函数
 * @param {string} text - 原始文本
 * @returns {string} - 适合数字人播报的文本
 */
export const filterForDigitalHuman = (text) => {
  if (!text || typeof text !== 'string') {
    return ''
  }

  let cleanText = text

  // 移除常见的系统提示词
  const systemPrompts = [
    /^(系统|System|AI|助手|Assistant)[:：]\s*/gi,
    /^(用户|User|Human)[:：]\s*/gi,
    /\[.*?\]/g, // 方括号内容
    /\{.*?\}/g, // 大括号内容
    /\(.*?\)/g, // 小括号内容（谨慎使用）
  ]

  systemPrompts.forEach(pattern => {
    cleanText = cleanText.replace(pattern, '')
  })

  // 移除代码相关内容
  cleanText = cleanText.replace(/```[\s\S]*?```/g, '') // 代码块
  cleanText = cleanText.replace(/`[^`]*`/g, '') // 行内代码
  cleanText = cleanText.replace(/function\s+\w+\s*\([^)]*\)/g, '') // 函数声明
  cleanText = cleanText.replace(/\w+\s*=\s*[^;]+;/g, '') // 赋值语句

  // 移除JSON格式内容
  cleanText = cleanText.replace(/\{[\s\S]*?\}/g, '')

  // 移除特殊标记
  cleanText = cleanText.replace(/#+\s*/g, '') // Markdown标题
  cleanText = cleanText.replace(/\*+/g, '') // 星号
  cleanText = cleanText.replace(/-{2,}/g, '') // 多个连字符
  cleanText = cleanText.replace(/_{2,}/g, '') // 多个下划线
  cleanText = cleanText.replace(/={2,}/g, '') // 多个等号

  // 移除网址和邮箱
  cleanText = cleanText.replace(/https?:\/\/[^\s]+/g, '')
  cleanText = cleanText.replace(/www\.[^\s]+/g, '')
  cleanText = cleanText.replace(/[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}/g, '')

  // 移除过多的标点符号
  cleanText = cleanText.replace(/[。！？]{3,}/g, '。')
  cleanText = cleanText.replace(/[,，]{3,}/g, '，')
  cleanText = cleanText.replace(/\.{3,}/g, '...')

  // 移除特殊字符但保留必要的标点
  cleanText = cleanText.replace(/[#@$%^&*+=\[\]{}|\\:";'<>?~`]/g, ' ')

  // 规范化空白字符
  cleanText = cleanText.replace(/\s+/g, ' ')
  cleanText = cleanText.replace(/\n\s*\n/g, '\n')
  cleanText = cleanText.trim()

  // 如果文本过短，返回空字符串
  if (cleanText.length < 2) {
    return ''
  }

  // 限制长度（数字人播报不宜过长）
  if (cleanText.length > 300) {
    cleanText = cleanText.substring(0, 300) + '...'
  }

  return cleanText
}

/**
 * 检查文本是否适合数字人播报
 * @param {string} text - 文本内容
 * @returns {Object} - 检查结果
 */
export const checkTextForDigitalHuman = (text) => {
  if (!text || typeof text !== 'string') {
    return {
      isValid: false,
      reason: '文本为空或格式不正确',
      suggestions: ['请提供有效的文本内容']
    }
  }

  const issues = []
  const suggestions = []

  // 检查长度
  if (text.length < 2) {
    issues.push('文本过短')
    suggestions.push('请提供更多内容')
  }

  if (text.length > 500) {
    issues.push('文本过长')
    suggestions.push('建议将内容控制在500字以内')
  }

  // 检查特殊字符比例
  const specialChars = text.match(/[#@$%^&*+=\[\]{}|\\:";'<>?~`]/g) || []
  const specialCharRatio = specialChars.length / text.length
  
  if (specialCharRatio > 0.3) {
    issues.push('包含过多特殊字符')
    suggestions.push('建议使用更自然的语言表达')
  }

  // 检查是否包含代码
  if (text.includes('```') || text.includes('function') || text.includes('var ') || text.includes('let ')) {
    issues.push('包含代码内容')
    suggestions.push('代码内容不适合语音播报')
  }

  // 检查是否包含URL
  if (text.match(/https?:\/\/[^\s]+/)) {
    issues.push('包含网址链接')
    suggestions.push('网址链接不适合语音播报')
  }

  return {
    isValid: issues.length === 0,
    reason: issues.join('、'),
    suggestions: suggestions,
    cleanText: filterForDigitalHuman(text)
  }
}

/**
 * 批量文本过滤
 * @param {Array} textArray - 文本数组
 * @param {Function} filterFunction - 过滤函数，默认使用filterForDigitalHuman
 * @returns {Array} - 过滤后的文本数组
 */
export const batchTextFilter = (textArray, filterFunction = filterForDigitalHuman) => {
  if (!Array.isArray(textArray)) {
    return []
  }

  return textArray
    .map(text => filterFunction(text))
    .filter(text => text && text.length > 0)
}

/**
 * 预设的文本过滤配置
 */
export const filterPresets = {
  // 严格模式：移除所有可能影响播报的内容
  strict: {
    removeEmoji: true,
    removeNumbers: false,
    removeEnglish: false,
    maxLength: 200
  },
  
  // 标准模式：保留基本内容，移除格式化字符
  standard: {
    removeEmoji: false,
    removeNumbers: false,
    removeEnglish: false,
    maxLength: 300
  },
  
  // 宽松模式：仅移除明显的干扰内容
  loose: {
    removeEmoji: false,
    removeNumbers: false,
    removeEnglish: false,
    maxLength: 500
  }
}

/**
 * 使用预设配置过滤文本
 * @param {string} text - 原始文本
 * @param {string} preset - 预设名称 ('strict', 'standard', 'loose')
 * @returns {string} - 过滤后的文本
 */
export const filterWithPreset = (text, preset = 'standard') => {
  const config = filterPresets[preset] || filterPresets.standard
  return smartTextClean(text, config)
}

// 默认导出
export default {
  basicTextClean,
  advancedTextClean,
  smartTextClean,
  filterForDigitalHuman,
  checkTextForDigitalHuman,
  batchTextFilter,
  filterPresets,
  filterWithPreset
} 