package com.hpu.xinqing.controller.ai;

import cn.dev33.satoken.jwt.SaJwtUtil;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hpu.xinqing.interceptor.WebSocketHandshakeAuth;
import com.hpu.xinqing.service.ai.ChatService;
import com.hpu.xinqingcommon.result.Result;
import com.hpu.xinqingpojo.entity.Chat;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@CrossOrigin
@RequestMapping("chat")
public class ChatController {

    @Autowired
    ChatService chatService;

    @Value("${sa-token.jwt-secret-key:xinqing}")
    private String jwtSecretKey;


    @GetMapping("history/{receiverId}/{currentPage}")
    public Result<?> getHistory(@PathVariable Long receiverId,
                                @PathVariable Integer currentPage,
                                @RequestParam @Nullable Integer pageSize,
                                HttpServletRequest request){

        String token = WebSocketHandshakeAuth.firstText(
                request.getHeader("token"),
                request.getParameter("token"),
                request.getHeader("Authorization")
        );
        Long userId = WebSocketHandshakeAuth.resolveSenderId(
                token,
                StpUtil::getLoginIdByToken,
                value -> SaJwtUtil.getLoginIdOrNull(value, "login", jwtSecretKey)
        );
        if (userId == null) {
            try {
                userId = Long.parseLong(StpUtil.getLoginId().toString());
            } catch (Exception ignored) {
                log.warn("Reject chat history request: unable to resolve login id, receiverId={}, currentPage={}",
                        receiverId, currentPage);
            }
        }
        if (userId == null) {
            return Result.error("未登录或 token 无效");
        }
        if (pageSize == null) pageSize=10;
        Page<Chat> page = new Page<>(currentPage,pageSize);
        return chatService.getHistory(userId,receiverId,page);
    }
}
