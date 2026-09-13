package com.hpu.xinqing.controller.ai;

import com.hpu.xinqing.manage.ClaudeManager.model.MentalAnalysisResult;
import com.hpu.xinqing.manage.ClaudeManager.service.MentalAnalysisService;
import com.hpu.xinqingcommon.result.Result;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.time.Duration;

@RestController
@RequestMapping("/claude")
@Slf4j
class AiClaudeController {

    //智能对话客户端
    @Resource
    private ChatClient chatClient;

    @Resource
    private MentalAnalysisService mentalAnalysisService;

    @GetMapping("/ai")
    public String generation(@RequestParam(value = "message", defaultValue = "你好") String message) {
        //提示词
        return chatClient.prompt()
                //用户输入
                .user(message)
                .call()
                .content();
    }

    //    @GetMapping(value = "/stream", produces = "text/html;charset=utf-8")
    @GetMapping(value = "/stream", produces = "text/html;charset=utf-8")
    public Flux<String> generationStream(@RequestParam(value = "message", defaultValue = "你好") String message, @RequestParam Long id) {
        return chatClient.prompt()
                .user(message)
                .stream()
                .content();
    }


    @GetMapping("/stream/t")
    public Flux<String> streamTest() {
        return Flux.just("hello", "d  asdsa ", " world", " kris")
                .delayElements(Duration.ofSeconds(1));
    }


    @GetMapping("analysis")
    public Result<MentalAnalysisResult> analysis(@RequestParam Long id) throws IOException {
        //判断id是否存在
        if (id == null) {
            return Result.error("请登录");
        }
        //提示词
        return Result.success(mentalAnalysisService.analyzeUserBehavior(id));
    }

}
