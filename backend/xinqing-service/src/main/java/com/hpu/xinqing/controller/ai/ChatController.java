package com.hpu.xinqing.controller.ai;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.hpu.xinqing.service.ai.ChatService;
import com.hpu.xinqingcommon.result.Result;
import com.hpu.xinqingpojo.entity.Chat;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@CrossOrigin
@RequestMapping("chat")
public class ChatController {

    @Autowired
    ChatService chatService;


    @GetMapping("history/{receiverId}/{currentPage}")
    public Result<?> getHistory(@PathVariable Long receiverId,
                                @PathVariable Integer currentPage,
                                @RequestParam @Nullable Integer pageSize){

        Long userId = Long.parseLong(StpUtil.getLoginId().toString());
        if (pageSize == null) pageSize=10;
        Page<Chat> page = new Page<>(currentPage,pageSize);
        return chatService.getHistory(userId,receiverId,page);
    }
}