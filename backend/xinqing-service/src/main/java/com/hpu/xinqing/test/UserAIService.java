package com.hpu.xinqing.test;

import com.hpu.xinqing.Embedding.Init.ToolUtils;
import dev.langchain4j.community.model.dashscope.QwenStreamingChatModel;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.UserMessage;
import reactor.core.publisher.Flux;

public class UserAIService {

    public interface Ai{
        String chat(@MemoryId Long userId, @UserMessage String content);
        Flux<String> getDate(String content);
    }




    public static void main(String[] args) {
        ChatMemory memory = MessageWindowChatMemory.withMaxMessages(10);

        QwenStreamingChatModel qwenModel = QwenStreamingChatModel.builder()
                .apiKey(System.getenv("DASHSCOPE_API_KEY"))
                .modelName("qwen-max")
                .build();

        Ai build = AiServices.builder(Ai.class)
                .streamingChatLanguageModel(qwenModel)
                .chatMemoryProvider(memoryId -> MessageWindowChatMemory.withMaxMessages(10))
                .tools(new ToolUtils())
                .build();

        System.out.println("-----------------");
//        TokenStream tokenStream = build.chatStream(1L, "现在是什么时间");
//        tokenStream.onCompleteResponse(response -> {
//            System.out.println(response);
//        }).onPartialResponse(message->{
//            System.out.println(message);
//        });
        Flux<String> flux = build.getDate("现在是什么时间");
        flux.subscribe(System.out::println);


    }

}
