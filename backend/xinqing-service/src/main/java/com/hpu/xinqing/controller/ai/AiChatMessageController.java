package com.hpu.xinqing.controller.ai;


import com.hpu.xinqing.service.ai.IAiChatMessageService;
import com.hpu.xinqingcommon.result.Result;
import com.hpu.xinqingpojo.entity.AiChatMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * AI 聊天消息表 前端控制器
 * </p>
 *
 * @since 2025-03-19
 */
@RestController
@RequestMapping("/ai-chat-message")
public class AiChatMessageController {

    @Autowired
    private IAiChatMessageService aiChatMessageService;

    //TODO:返回对应的回话中的聊天信息,返回最近的20条数据
    @GetMapping("/selectMemory")
    public Result selectMemory(@RequestParam("memoryId") String memoryId
                               ,@RequestParam("startTime") LocalDateTime startTime

    ){
        List<AiChatMessage> list=aiChatMessageService.selectByMemoryId(memoryId,startTime);
        return Result.success(list);
    }

}
