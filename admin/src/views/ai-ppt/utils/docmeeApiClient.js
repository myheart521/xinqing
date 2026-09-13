/**
 * 文多多 API 客户端工具类
 */
export class DocmeeApiClient {
  constructor(apiKey = '') {
    this.apiKey = apiKey;
    this.baseUrl = 'https://docmee.cn/api';
  }

  /**
   * 创建API Token
   * @param {string} uid - 用户ID，不同uid创建的token数据会相互隔离
   * @param {number|null} limit - 限制token最大生成PPT次数
   * @returns {Promise<string>} token
   */
  async createApiToken(uid = 'default_user', limit = null) {
    if (!this.apiKey) {
      throw new Error('请设置API Key');
    }

    const url = `${this.baseUrl}/user/createApiToken`;
    
    try {
      const response = await fetch(url, {
        method: 'POST',
        headers: {
          'Api-Key': this.apiKey,
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({ uid, limit })
      });

      const result = await response.json();
      
      if (result.code !== 0) {
        throw new Error(`创建token异常：${result.message}`);
      }
      
      return result.data.token;
    } catch (error) {
      console.error('创建API Token失败:', error);
      throw error;
    }
  }

  /**
   * 解析文件数据
   * @param {File} file - 文件对象
   * @param {string} token - API token
   * @returns {Promise<string>} dataUrl
   */
  async parseFileData(file, token) {
    if (!token) {
      throw new Error('Token不能为空');
    }

    const formData = new FormData();
    formData.append('file', file);

    const url = `${this.baseUrl}/ppt/parseFileData`;
    
    try {
      const response = await fetch(url, {
        method: 'POST',
        headers: {
          'token': token
        },
        body: formData
      });

      const result = await response.json();
      
      if (result.code !== 0) {
        throw new Error(`解析文件异常：${result.message}`);
      }
      
      return result.data.dataUrl;
    } catch (error) {
      console.error('解析文件失败:', error);
      throw error;
    }
  }

  /**
   * 提取文件大纲
   * @param {File} file - 文件对象
   * @param {string} token - API token
   * @returns {Promise<{dataUrl: string, outlineText: string}>}
   */
  async extractFileOutline(file, token) {
    if (!token) {
      throw new Error('Token不能为空');
    }

    const formData = new FormData();
    formData.append('file', file);

    const url = `${this.baseUrl}/ppt/extractFileOutline?format=text`;
    
    try {
      const response = await fetch(url, {
        method: 'POST',
        headers: {
          'token': token
        },
        body: formData
      });

      const result = await response.json();
      
      if (result.code !== 0) {
        throw new Error(`提取文件大纲异常：${result.message}`);
      }
      
      return {
        dataUrl: result.data.dataUrl,
        outlineText: result.data.outlineText
      };
    } catch (error) {
      console.error('提取文件大纲失败:', error);
      throw error;
    }
  }
}

// 导出单例
export const docmeeApiClient = new DocmeeApiClient(); 