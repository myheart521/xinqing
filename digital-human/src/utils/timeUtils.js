/**
 * 格式化时间为可读的字符串
 * @param {string|Date} time 时间数据
 * @returns {string} 格式化后的时间字符串
 */
export function formatTime(time) {
  if (!time) return '未知时间';

  try {
    // 直接使用原生 Date 对象处理时区问题
    const date = new Date(time);
    
    // 检查是否是有效日期
    if (isNaN(date.getTime())) {
      throw new Error('Invalid date format');
    }
    
    // 直接使用本地时区显示，不做 UTC+8 的手动转换
    // 这样前端显示时会根据用户系统的本地时区自动调整
    const year = date.getFullYear();
    const month = (date.getMonth() + 1).toString().padStart(2, '0');
    const day = date.getDate().toString().padStart(2, '0');
    const hours = date.getHours().toString().padStart(2, '0');
    const minutes = date.getMinutes().toString().padStart(2, '0');

    return `${year}-${month}-${day} ${hours}:${minutes}`;
  } catch (e) {
    console.error('格式化时间失败:', e, time);
    // 如果格式化失败，返回原始字符串或默认值
    return typeof time === 'string' ? time : '未知时间';
  }
}

/**
 * 获取中国标准时间（UTC+8）的ISO字符串
 * @returns {string} ISO格式的中国标准时间
 */
export function getChinaTime() {
  const now = new Date();
  // 获取当前时间的时间戳
  const timestamp = now.getTime();
  // 添加8小时的毫秒数
  const chinaTimestamp = timestamp + (8 * 60 * 60 * 1000);
  // 创建一个新的日期对象，使用调整后的时间戳
  const chinaTime = new Date(chinaTimestamp);
  // 返回ISO格式的字符串
  return chinaTime.toISOString();
}

/**
 * 生成一个唯一的会话ID
 * @returns {string} 唯一ID
 */
export function generateMemoryId() {
  return Date.now().toString() + '-' + Math.random().toString(36).substr(2, 9);
}