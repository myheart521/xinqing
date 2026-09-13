package com.hpu.xinqing.controller.ai;


import com.hpu.xinqing.service.ai.IAiChatMemoryService;
import com.hpu.xinqingcommon.result.Result;
import com.hpu.xinqingpojo.entity.AiChatMemory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 聊天回话表 前端控制器
 * </p>
 *
 * @since 2025-03-20
 */
@RestController
@RequestMapping("/ai-chat-memory")
public class AiChatMemoryController {

    @Autowired
    private IAiChatMemoryService aiChatMemoryService;



    @GetMapping("/list")
    public Result getMemoryList(){
        List<AiChatMemory> list=aiChatMemoryService.getByUserId();
        return Result.success(list);
    }

}
