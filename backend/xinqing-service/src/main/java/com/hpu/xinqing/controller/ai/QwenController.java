package com.hpu.xinqing.controller.ai;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.ObjectUtil;
import com.hpu.xinqing.manage.TeacherAgent.tools.TeacherBasicTools;
import com.hpu.xinqing.service.ai.Assistant;
import com.hpu.xinqing.service.ai.AssistantTeacher;
import com.hpu.xinqing.service.ai.AssistantTeacherMCP;
import com.hpu.xinqing.service.ai.AssistantWeb;
import com.hpu.xinqing.service.ai.IAiChatMemoryService;
import com.hpu.xinqingcommon.result.Result;
import com.hpu.xinqingcommon.utils.AIUtil;
import dev.langchain4j.service.TokenStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/qwen")
@RequiredArgsConstructor
@Slf4j
public class QwenController {

    private final Assistant assistant;
    private final AssistantWeb assistantWeb;
    private final AssistantTeacher assistantTeacher;
    private final AssistantTeacherMCP assistantTeacherMCP;
    private final TeacherBasicTools teacherBasicTools;
    private final IAiChatMemoryService aiChatMemoryService;

    @GetMapping("/stream/token")
    public TokenStream streamToken(@RequestParam("prompt") String prompt) {
        return assistant.streamChat(prompt);
    }

    @GetMapping("/stream/flux")
    public Flux<String> streamFlux(@RequestParam("prompt") String prompt,
                                   @RequestParam(value = "memoryId") String memoryId) {
        aiChatMemoryService.createAndUpdate(memoryId, prompt);
        return assistant.fluxChat(memoryId, prompt);
    }

    @GetMapping("/web/stream/flux")
    public Flux<String> streamFluxWeb(@RequestParam("prompt") String prompt,
                                      @RequestParam(value = "memoryId") String memoryId) {
        aiChatMemoryService.createAndUpdate(memoryId, prompt);
        return assistantWeb.fluxChat(prompt);
    }

    @GetMapping("/ollama/stream/flux")
    public Flux<String> streamFlux2(@RequestParam("prompt") String prompt,
                                    @RequestParam(value = "memoryId") String memoryId) {
        aiChatMemoryService.createAndUpdate(memoryId, prompt);
        return assistantTeacher.fluxChat(memoryId, prompt);
    }

    @GetMapping("teacher/stream/flux")
    public Flux<String> streamFluxMain(@RequestParam("prompt") String prompt,
                                       @RequestParam(value = "memoryId") String memoryId) {
        Long userId = Long.parseLong(StpUtil.getLoginId().toString());
        if (ObjectUtil.isEmpty(userId)) {
            throw new RuntimeException("\u8bf7\u5148\u767b\u5f55");
        }

        aiChatMemoryService.createAndUpdate(memoryId, prompt);

        if (StudentToolIntentResolver.shouldDirectLookupStudentInfo(prompt)) {
            String studentNumber = StudentToolIntentResolver.extractStudentNumber(prompt);
            try {
                return Flux.just(teacherBasicTools.getStudentInfo(studentNumber));
            } catch (Exception e) {
                log.error("Direct student info lookup failed, fallback to assistantTeacher", e);
            }
        }

        prompt = "\u767b\u5f55\u7528\u6237id\u4e3a:" + userId + "\u3002" + prompt;
        return Flux.just(assistantTeacher.chatMain(memoryId, prompt));
    }

    @GetMapping("teacher/stream/flux/mcp")
    public String streamFluxMCP(@RequestParam("prompt") String prompt,
                                @RequestParam(value = "memoryId") String memoryId) {
        Long userId = Long.parseLong(StpUtil.getLoginId().toString());
        if (ObjectUtil.isEmpty(userId)) {
            throw new RuntimeException("\u8bf7\u5148\u767b\u5f55");
        }
        return assistantTeacherMCP.chat(memoryId, prompt);
    }

    @GetMapping("/tts")
    public Result<String> tts(@RequestParam("text") String text) {
        String audioUrl = AIUtil.qwenTTS(text);
        if (ObjectUtil.isEmpty(audioUrl)) {
            throw new RuntimeException("\u51fa\u73b0\u5f02\u5e38");
        }
        return Result.success(audioUrl);
    }
}
