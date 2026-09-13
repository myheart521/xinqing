import request from '@/utils/request';
import {requestUrl as myRequestUrl} from "@/utils/URL.js";

// 接口路径常量定义 - 基于最新API文档更新
const AI_CHAT_API = '/qwen/web/stream/flux'; // 联网搜索接口
const ADMIN_AI_AGENT_API = '/qwen/teacher/stream/flux'; // 管理端智能体入口接口
const ADMIN_MCP_AI_AGENT_API = '/qwen/teacher/stream/flux/mcp'; // 管理端MCP智能体入口接口
const CHAT_MEMORY_LIST_API = '/ai-chat-memory/list'; // 获取所有聊天会话列表接口
const CHAT_MESSAGE_SELECT_API = '/ai-chat-message/selectMemory'; // 获取指定聊天会话的消息接口

/**
 * 发送联网搜索消息
 * @param {string} prompt - 用户输入的查询内容
 * @param {string|null} memoryId - 会话上下文 ID，可选，用于保持对话连贯性
 * @returns {Promise} 返回一个支持流式响应的Promise对象
 */
export const sendAIChatMessage = (prompt, memoryId = null) => {
    const params = { prompt };
    if (memoryId) {
        params.memoryId = memoryId;
        console.log('发送消息使用memoryId:', memoryId);
    } else {
        console.warn('发送消息没有提供memoryId');
    }
    
    const requestUrl = myRequestUrl +AI_CHAT_API + '?' + new URLSearchParams(params).toString();
    console.log('发送请求URL:', requestUrl);

    return fetch(requestUrl, {
        method: 'GET',
        headers: {
            'Accept': 'text/event-stream',
            'token': localStorage.getItem('accessToken') || '',
        },
    }).then(response => {
        if (!response.ok) {
            throw new Error(`HTTP error: ${response.status}`);
        }
        console.log('请求成功，状态码:', response.status);
        return response;
    });
};

/**
 * 发送管理端智能体入口消息
 * @param {string} prompt - 用户输入的查询内容
 * @param {string} memoryId - 会话上下文 ID，必须提供
 * @returns {Promise} 返回一个支持流式响应的Promise对象
 */
export const sendAdminAIAgentMessage = (prompt, memoryId) => {
    const params = {
        prompt,
        memoryId
    };
    
    console.log('管理端智能体 - 发送消息参数:', { prompt, memoryId });
    const requestUrl = myRequestUrl+ADMIN_AI_AGENT_API + '?' + new URLSearchParams(params).toString();
    console.log('管理端智能体 - 发送请求URL:', requestUrl);

    return fetch(requestUrl, {
        method: 'GET',
        headers: {
            'Accept': 'text/event-stream',
            'token': localStorage.getItem('accessToken') || '',
        },
    }).then(response => {
        if (!response.ok) {
            throw new Error(`HTTP error: ${response.status}`);
        }
        console.log('管理端智能体 - 请求成功，状态码:', response.status);
        return response;
    });
};

/**
 * 发送管理端MCP智能体入口消息
 * @param {string} prompt - 用户输入的查询内容
 * @param {string} memoryId - 会话上下文 ID，必须提供
 * @returns {Promise} 返回一个支持流式响应的Promise对象
 */
export const sendAdminMCPAIAgentMessage = (prompt, memoryId) => {
    const params = {
        prompt,
        memoryId
    };
    
    console.log('管理端MCP智能体 - 发送消息参数:', { prompt, memoryId });
    const requestUrl = myRequestUrl+ADMIN_MCP_AI_AGENT_API + '?' + new URLSearchParams(params).toString();
    console.log('管理端MCP智能体 - 发送请求URL:', requestUrl);

    return fetch(requestUrl, {
        method: 'GET',
        headers: {
            'Accept': 'text/event-stream',
            'token': localStorage.getItem('accessToken') || '',
        },
    }).then(response => {
        if (!response.ok) {
            throw new Error(`HTTP error: ${response.status}`);
        }
        console.log('管理端MCP智能体 - 请求成功，状态码:', response.status);
        return response;
    });
};

/**
 * 获取所有用户聊天会话
 * @returns {Promise} 返回包含所有聊天会话的Promise对象
 */
export const fetchAllChatSessions = () => {
    console.log('正在请求历史会话列表:', CHAT_MEMORY_LIST_API);
    // 优先使用accessToken，这应该是主要使用的token
    const token = localStorage.getItem('accessToken') || localStorage.getItem('token') || '';

    return request({
        url: myRequestUrl+CHAT_MEMORY_LIST_API,
        method: 'get',
        headers: {
            token: token
        }
    }).then(response => {
        console.log('历史会话列表请求成功，原始响应:', response);
        return response;
    }).catch(error => {
        console.error('历史会话列表请求失败:', error);
        console.error('请求参数:', CHAT_MEMORY_LIST_API, '使用的token:', token);
        throw error;
    });
};

/**
 * 获取指定聊天会话的消息
 * @param {string} memoryId - 会话上下文 ID
 * @param {string} startTime - 会话开始时间（查询此时间之前的消息）
 * @returns {Promise} 返回包含指定会话消息的Promise对象
 */
export const fetchChatMessages = (memoryId, startTime) => {
    // 获取token
    const token = localStorage.getItem('accessToken') || '';
    console.log('原始startTime参数:', startTime);

    // 简化日期格式 - 从ISO格式中提取YYYY-MM-DDThh:mm:ss部分
    let simpleStartTime = startTime;
    try {
        console.log('处理前的时间格式:', startTime, '时间类型:', typeof startTime);
        
        if (startTime && startTime.includes('.')) {
            simpleStartTime = startTime.split('.')[0];
            console.log('简化去除毫秒后的日期格式:', simpleStartTime);
        } 
        
        if (startTime && startTime.includes('Z')) {
            simpleStartTime = startTime.replace('Z', '');
            console.log('简化去除Z后的日期格式:', simpleStartTime);
        }
        
        // 转换为Date对象再输出，检查时间是否正确
        const dateObj = new Date(simpleStartTime);
        console.log('处理后时间解析为Date对象:', dateObj.toString());
        console.log('对应的本地时间:', dateObj.toLocaleString());
    } catch (e) {
        console.error('日期格式处理失败:', e);
    }

    // 尝试使用更简单的URL方式
    const baseUrl = myRequestUrl+CHAT_MESSAGE_SELECT_API;
    console.log('原始URL:', baseUrl);

    // 方法1：直接添加参数到URL
    const url = `${baseUrl}?memoryId=${memoryId}&startTime=${simpleStartTime}`;
    console.log('最终请求URL:', url);

    // 使用XMLHttpRequest直接发送请求
    return new Promise((resolve, reject) => {
        const xhr = new XMLHttpRequest();
        xhr.open('GET', url, true);
        xhr.setRequestHeader('token', token);
        xhr.setRequestHeader('Accept', 'application/json');

        xhr.onload = function() {
            if (xhr.status >= 200 && xhr.status < 300) {
                try {
                    const response = JSON.parse(xhr.responseText);
                    console.log('请求成功:', response);
                    if (response.data && Array.isArray(response.data)) {
                        resolve({ data: response.data });
                    } else if (response.data && response.data.data && Array.isArray(response.data.data)) {
                        resolve({ data: response.data.data });
                    } else if (Array.isArray(response)) {
                        resolve({ data: response });
                    } else {
                        console.warn('响应数据格式不符合预期:', response);
                        resolve({ data: [] });
                    }
                } catch (e) {
                    console.error('解析响应失败:', e);
                    resolve({ data: [] });
                }
            } else {
                console.error('请求失败, 状态码:', xhr.status);
                console.error('响应文本:', xhr.responseText);
                resolve({
                    data: [],
                    code: 1,
                    msg: `请求失败，状态码: ${xhr.status}`
                });
            }
        };

        xhr.onerror = function() {
            console.error('请求发生网络错误');
            resolve({
                data: [],
                code: 1,
                msg: '网络错误'
            });
        };

        xhr.send();
    });
};

/**
 * 加载更多历史消息
 * @param {string} memoryId 会话ID
 * @param {string} startTime 起始时间，保持原始格式
 * @returns {Promise<Object>} 消息列表
 */
export function loadMoreMessages(memoryId, startTime) {
    // 与fetchChatMessages使用相同的方法
    return fetchChatMessages(memoryId, startTime);
}

/**
 * 提供本地模拟数据的测试函数，在API失败时使用
 * @param {string} memoryId 会话ID
 * @returns {array} 模拟的消息数据
 */
export function getTestData(memoryId) {
  console.log('使用本地测试数据代替API调用');
  return [
    {
      role: 'user',
      content: '你好，我是用户',
      createTime: new Date().toISOString()
    },
    {
      role: 'assistant',
      content: '你好！我是AI助手，很高兴为您服务。请问有什么可以帮助您的？',
      createTime: new Date().toISOString()
    },
    {
      role: 'user',
      content: '请给我讲一个笑话',
      createTime: new Date().toISOString()
    },
    {
      role: 'assistant',
      content: '当然！这是一个笑话：\n\n有一个程序员走进一家咖啡店，他看到菜单上写着：\n咖啡：￥5\n加奶：+￥1\n加糖：+￥0.5\n\n他回复："Undefined. No sugar? I need coffee."',
      createTime: new Date().toISOString()
    }
  ];
}

/**
 * 手动测试API，尝试多种不同的参数格式
 * @param {string} memoryId 会话ID
 * @returns {Promise} 测试结果
 */
export function testAPI(memoryId) {
  console.log('开始测试API');
  // 获取当前日期
  const currentDate = new Date();

  // 生成多种格式的日期
  const dates = [
    currentDate.toISOString(), // 完整ISO格式：2023-01-01T12:00:00.000Z
    currentDate.toISOString().split('.')[0], // 无毫秒：2023-01-01T12:00:00
    `${currentDate.getFullYear()}-${String(currentDate.getMonth() + 1).padStart(2, '0')}-${String(currentDate.getDate()).padStart(2, '0')}T${String(currentDate.getHours()).padStart(2, '0')}:${String(currentDate.getMinutes()).padStart(2, '0')}:${String(currentDate.getSeconds()).padStart(2, '0')}`, // 自定义格式
    currentDate.toISOString().replace('T', ' ').split('.')[0], // 空格分隔：2023-01-01 12:00:00
  ];

  console.log('测试的日期格式:', dates);

  // 获取token
  const token = localStorage.getItem('accessToken') || '';

  // 测试每种日期格式
  const promises = dates.map(date => {
    const url = `${CHAT_MESSAGE_SELECT_API}?memoryId=${memoryId}&startTime=${date}`;
    console.log(`测试URL: ${url}`);

    return new Promise(resolve => {
      const xhr = new XMLHttpRequest();
      xhr.open('GET', url, true);
      xhr.setRequestHeader('token', token);
      xhr.setRequestHeader('Accept', 'application/json');

      xhr.onload = function() {
        resolve({
          date,
          status: xhr.status,
          success: xhr.status >= 200 && xhr.status < 300,
          response: xhr.responseText
        });
      };

      xhr.onerror = function() {
        resolve({
          date,
          status: 'network error',
          success: false,
          response: null
        });
      };

      xhr.send();
    });
  });

  return Promise.all(promises).then(results => {
    console.log('API测试结果:', results);
    return results;
  });
}
