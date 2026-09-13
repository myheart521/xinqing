package com.hpu.xinqing.interceptor;

import cn.dev33.satoken.stp.StpUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;
@Component
public class MyHandShakeInterceptor implements HandshakeInterceptor {



    //TODO 发送的请求一定要带上token
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        //验证两个参数是好友关系

        //如果验证通过，则将好友关系存储到attributes中
        if (request instanceof ServletServerHttpRequest){
            ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
            HttpServletRequest httpRequest = servletRequest.getServletRequest();
            String token = httpRequest.getParameter("token");
            Long userId=Long.parseLong((String) StpUtil.getLoginIdByToken(token));
            System.out.println("userId = " + userId);
            Long receiverId = Long.parseLong(httpRequest.getParameter("receiverId"));
            attributes.put("senderId",userId);
            attributes.put("receiverId",receiverId);
            return true;
        }
        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

    }
}
