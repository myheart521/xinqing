/**
 * LocalStorage 工具类
 * 提供对本地存储的简便操作方法
 */
export const LocalStorage = {
  /**
   * 存储数据到 localStorage
   * @param {string} key - 存储的键名
   * @param {any} value - 要存储的数据，将自动转换为 JSON 字符串
   */
  set(key, value) {
    try {
      const stringValue = typeof value === 'object' 
        ? JSON.stringify(value) 
        : String(value);
      localStorage.setItem(key, stringValue);
    } catch (error) {
      console.error('LocalStorage 存储失败:', error);
    }
  },

  /**
   * 从 localStorage 获取数据
   * @param {string} key - 要获取的键名
   * @param {any} defaultValue - 如果键不存在时返回的默认值
   * @returns {any} 解析后的值或默认值
   */
  get(key, defaultValue = null) {
    try {
      const value = localStorage.getItem(key);
      if (value === null) return defaultValue;
      
      // 尝试解析 JSON
      try {
        return JSON.parse(value);
      } catch {
        // 如果解析失败，返回原始字符串
        return value;
      }
    } catch (error) {
      console.error('LocalStorage 获取失败:', error);
      return defaultValue;
    }
  },

  /**
   * 从 localStorage 移除指定的键
   * @param {string} key - 要移除的键名
   */
  remove(key) {
    try {
      localStorage.removeItem(key);
    } catch (error) {
      console.error('LocalStorage 移除失败:', error);
    }
  },

  /**
   * 清空所有 localStorage 数据
   */
  clear() {
    try {
      localStorage.clear();
    } catch (error) {
      console.error('LocalStorage 清空失败:', error);
    }
  },

  /**
   * 检查某个键是否存在于 localStorage 中
   * @param {string} key - 要检查的键名
   * @returns {boolean} 是否存在
   */
  exists(key) {
    return localStorage.getItem(key) !== null;
  }
};

export default LocalStorage;