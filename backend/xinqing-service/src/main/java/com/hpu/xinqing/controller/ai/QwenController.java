package com.hpu.xinqing.controller.ai;


import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.ObjectUtil;
import com.hpu.xinqing.service.ai.*;
import com.hpu.xinqing.service.ai.Assistant;
import com.hpu.xinqing.service.ai.AssistantTeacher;
import com.hpu.xinqing.service.ai.AssistantWeb;
import com.hpu.xinqing.service.ai.IAiChatMemoryService;

import com.hpu.xinqingcommon.result.Result;
import com.hpu.xinqingcommon.utils.AIUtil;
import com.hpu.xinqingpojo.entity.User;
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
    private final AssistantTeacher assistantMCP;
    private final AssistantTeacherMCP assistantTeacherMCP;
    //    private final QwenStreamingChatModel streamingChatModel;
    private final IAiChatMemoryService aiChatMemoryService;


    //这个是简单 low-hevle的方法，
  /*  @GetMapping("/stream")
    public Flux<String> stream(@RequestParam("prompt") String prompt, HttpServletResponse response) {
        response.setCharacterEncoding("UTF-8");
        ChatRequest chatRequest = ChatRequest.builder()
                .messages(SystemMessage.systemMessage("你是是由项目贡献者开发的专注大学生心理健康的智能体,请你以专业心理医生的角度回答问题")
                        , UserMessage.userMessage(prompt)
                ).build();
        return Flux.create(sink -> {
            streamingChatModel.chat(chatRequest, new StreamingChatResponseHandler() {
                @Override
                public void onPartialResponse(String partialResponse) {
                    log.info("onPartialResponse:{}", partialResponse);
                    sink.next(partialResponse);
                }

                @Override
                public void onCompleteResponse(ChatResponse completeResponse) {
                    log.info("complete:{}", completeResponse);
                    sink.complete();
                }

                @Override
                public void onError(Throwable error) {
                    sink.error(error);
                }
            });
        });
    }
*/
    //TODO:不行,需要改
    @GetMapping("/stream/token")
    public TokenStream streamToken(@RequestParam("prompt") String prompt) {
        TokenStream tokenStream = assistant.streamChat(prompt);
//        tokenStream.onCompleteResponse(FluxSink)
        //tokenStream中提供的有获得流式输出过程的方法，最终结果的方法
        return tokenStream;
    }

    //使用AiService,实现回话记忆,同时RAG
    @GetMapping("/stream/flux")
    public Flux<String> streamFlux(@RequestParam("prompt") String prompt
            , @RequestParam(value = "memoryId") String memoryId
//                                   ,@RequestParam(value = "aiModel") String aiModel
    ) {
//        Assistant assistant=null;
//        for (String name: AssistantInit.supportAIList){
//            if (aiModel.equals(name)){
//                Assistant bean = (Assistant) SpringUtil.getBean(name);
//            }
//        }
        //根据第一次问题生成会话,前端生成一个UUID
        aiChatMemoryService.createAndUpdate(memoryId, prompt);
        return assistant.fluxChat(memoryId, prompt);

    }

    //使用AiService,实现回话记忆
    @GetMapping("/web/stream/flux")
    public Flux<String> streamFluxWeb(@RequestParam("prompt") String prompt
            , @RequestParam(value = "memoryId") String memoryId
    ) {
        //根据第一次问题生成会话,前端生成一个UUID
        aiChatMemoryService.createAndUpdate(memoryId, prompt);
        return assistantWeb.fluxChat(prompt);
    }

    @GetMapping("/ollama/stream/flux")
    public Flux<String> streamFlux2(@RequestParam("prompt") String prompt
            , @RequestParam(value = "memoryId") String memoryId
//                                   ,@RequestParam(value = "aiModel") String aiModel
    ) {

        //根据第一次问题生成会话,前端生成一个UUID
        aiChatMemoryService.createAndUpdate(memoryId, prompt);
        return assistantTeacher.fluxChat(memoryId, prompt);

    }

    /**
     * 老师端智能体接口
     */
    @GetMapping("teacher/stream/flux")
    public Flux<String> streamFluxMain(@RequestParam("prompt") String prompt, @RequestParam(value = "memoryId") String memoryId) {
        //检查是否登录
        Long userId = Long.parseLong(StpUtil.getLoginId().toString());
        if (ObjectUtil.isEmpty(userId)) {
            throw new RuntimeException("请先登录");
        }
        aiChatMemoryService.createAndUpdate(memoryId, prompt);
        //获取登录用户的id方便文件传输
        prompt = "登录用户id为:"+userId+"。"+prompt;
        return assistantTeacher.fluxChatMain(memoryId, prompt);
    }

    /**
     * 老师端基于mcp的AI
     * 这个只是测试用，用的不是流式输出，千万不要改为流式输出！！！
     */
    @GetMapping("teacher/stream/flux/mcp")
    public String streamFluxMCP(@RequestParam("prompt") String prompt, @RequestParam(value = "memoryId") String memoryId) {
        //检查是否登录
        Long userId = Long.parseLong(StpUtil.getLoginId().toString());
        if (ObjectUtil.isEmpty(userId)) {
            throw new RuntimeException("请先登录");
        }
        return assistantTeacherMCP.chat(memoryId, prompt);
    }

    @GetMapping("/tts")
    public Result<String> tts(@RequestParam("text") String text) {
        String audioUrl = AIUtil.qwenTTS(text);
        if (ObjectUtil.isEmpty(audioUrl)) {
            throw new RuntimeException("出现异常");
        }
        return Result.success(audioUrl);
    }

}
