package com.hpu.xinqing.config;


import com.hpu.xinqing.interceptor.MyHandShakeInterceptor;
import com.hpu.xinqing.websocket.U2UChat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import java.util.HashMap;

@Component
@EnableWebSocket
@DependsOn("u2UChat")
public class WebSocketConfig implements WebSocketConfigurer {
    @Autowired
    U2UChat u2UChat;

    @Autowired
    MyHandShakeInterceptor myHandShakeInterceptor;


    /**
     * 请求示例:
     * new WebSocket(ws://xinqing/chat?token={token}&receiverId={receiverId})
     */

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(u2UChat,"/xinqing/chat")
                .addInterceptors(myHandShakeInterceptor)
                .setAllowedOrigins("*");
    }
}
