import {request} from "@/utils/request";
import {webSocketUrl} from "@/utils/URL.js";

// 通用错误处理函数
const handleError = (error, message) => {
    console.error(message, error);
    throw new Error(message);
};

// 获取聊天记录
export const getChatMessages = async (receiverId, currentPage = 1, pageSize = 5) => {
    try {
        const response = await request(`/chat/history/${receiverId}/${currentPage}`, {
            params: {pageSize},
            'token': localStorage.getItem('accessToken'),
        });
        console.log('聊天响应:', response); // 输出完整响应，便于调试
        if (response.code === 1 && response.data && Array.isArray(response.data.history)) {
            console.log(`接收者 ${receiverId} 的聊天记录:`, response.data.history);
            return response.data.history; // 返回 history 数组
        } else {
            console.warn(`接收者 ${receiverId} 的聊天记录未找到或格式错误:`, {
                code: response.code,
                data: response.data,
            });
            return [];
        }
    } catch (error) {
        handleError(error, `获取接收者 ${receiverId} 的聊天记录时出错`);
        return []; // 捕获错误时返回空数组，避免中断流程
    }
};

// 创建 WebSocket 连接
export const createWebSocketConnection = (token, receiverId) => {
    const senderId = JSON.parse(atob(token.split('.')[1])).loginId;
    const wsUrl = webSocketUrl + `?token=${token}&senderId=${senderId}&receiverId=${receiverId}`;
    console.log('尝试连接 WebSocket:', wsUrl);
    const ws = new WebSocket(wsUrl);

    ws.onopen = () => {
        console.log('WebSocket 连接已建立');
    };

    ws.onmessage = (event) => {
        console.log('收到消息: ', event.data);
    };

    ws.onclose = (event) => {
        console.log('WebSocket 连接已关闭, code:', event.code);
    };

    ws.onerror = (error) => {
        console.error('WebSocket 发生错误: ', error);
    };

    return ws;
};

// 发送消息
export const sendMessage = (ws, message) => {
    if (ws && ws.readyState === WebSocket.OPEN) {
        ws.send(message);
        console.log('发送消息:', message);
    } else {
        console.error('WebSocket 连接未建立');
    }
};
