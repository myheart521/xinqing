package com.hpu.xinqing.interceptor;

import cn.dev33.satoken.jwt.SaJwtUtil;
import cn.dev33.satoken.stp.StpUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Component
@Slf4j
public class MyHandShakeInterceptor implements HandshakeInterceptor {

    private static final String SA_TOKEN_LOGIN_TYPE = "login";

    @Value("${sa-token.jwt-secret-key:xinqing}")
    private String jwtSecretKey;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) {
        if (!(request instanceof ServletServerHttpRequest servletRequest)) {
            return false;
        }

        HttpServletRequest httpRequest = servletRequest.getServletRequest();
        String token = WebSocketHandshakeAuth.firstText(
                httpRequest.getParameter("token"),
                httpRequest.getHeader("token"),
                httpRequest.getHeader("Authorization")
        );
        String senderIdParameter = httpRequest.getParameter("senderId");
        String receiverIdParameter = httpRequest.getParameter("receiverId");

        Long senderId = WebSocketHandshakeAuth.resolveSenderId(
                token,
                StpUtil::getLoginIdByToken,
                value -> SaJwtUtil.getLoginIdOrNull(value, SA_TOKEN_LOGIN_TYPE, jwtSecretKey)
        );
        Long receiverId = WebSocketHandshakeAuth.parseLongOrNull(receiverIdParameter);

        if (senderId == null) {
            log.warn("Reject websocket handshake: invalid token, path={}", httpRequest.getRequestURI());
            return false;
        }
        if (receiverId == null) {
            log.warn("Reject websocket handshake: invalid receiverId, path={}", httpRequest.getRequestURI());
            return false;
        }
        if (!WebSocketHandshakeAuth.isSenderIdConsistent(senderId, senderIdParameter)) {
            log.warn("Reject websocket handshake: senderId parameter does not match authenticated user, path={}",
                    httpRequest.getRequestURI());
            return false;
        }

        attributes.put("senderId", senderId);
        attributes.put("receiverId", receiverId);
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
                               Exception exception) {
    }
}
