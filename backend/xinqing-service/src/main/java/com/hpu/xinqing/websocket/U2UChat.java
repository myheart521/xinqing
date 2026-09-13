package com.hpu.xinqing.websocket;


import com.hpu.xinqing.utils.mq.u2uChat.CreateQueueUtils;
import com.hpu.xinqing.utils.mq.u2uChat.DynamicListener;
import com.hpu.xinqing.mapper.ChatMapper;
import com.hpu.xinqingpojo.entity.Chat;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class U2UChat extends TextWebSocketHandler {


    @Autowired
    CreateQueueUtils createQueueUtils;
    @Autowired
    DynamicListener dynamicListener;
    @Resource
    ChatMapper chatMapper;


    // 用来存储所有连接用户的 WebSocketSession
    protected static final Map<Long, WebSocketSession> sessionMap = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session)  {

        //获取到请求参数
        Map<String, Object> attributes = session.getAttributes();
        //存储session关系
        sessionMap.put(senderId(session),session);
        //创建一个队列并绑定
        try {
            createQueueUtils.bindU2UQueue(receiverId(session));
            dynamicListener.addListener("u2u-chat-user-"+receiverId(session));
        } catch (Exception e) {
            log.warn("WebSocket legacy queue listener init failed, senderId={}, receiverId={}",
                    senderId(session), receiverId(session), e);
        }
        System.out.println("登录成功");
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage msgContent) throws Exception {
        String content = msgContent.getPayload();
        Long senderId = senderId(session);
        Long receiverId = receiverId(session);
        WebSocketSession objSession = sessionMap.get(receiverId);
        if (objSession != null && objSession.isOpen()) {
            objSession.sendMessage(new TextMessage(content));
        } else if (objSession != null) {
            sessionMap.remove(receiverId);
        }
        chatMapper.insert(Chat.insertChat(senderId, receiverId, content));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        //删除用户在线的session
        sessionMap.remove(senderId(session));
        log.info("已退出连接, 关闭连接");
        log.info("结束的状态码是:{}", status.getCode());
    }


    private Long senderId(WebSocketSession session) {
        return (Long) session.getAttributes().get("senderId");
    }
    private Long receiverId(WebSocketSession session) {
        return (Long) session.getAttributes().get("receiverId");
    }
}
