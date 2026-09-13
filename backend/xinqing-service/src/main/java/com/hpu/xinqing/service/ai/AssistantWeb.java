package com.hpu.xinqing.service.ai;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.UserMessage;
import reactor.core.publisher.Flux;

public interface AssistantWeb {

    @SystemMessage("你是是由项目贡献者开发的专注大学生心理健康的智能体," +
            "请你以专业心理医生的角度回答问题")
    String chat(String message);

//    通过注解来表示对应参数是什么数据
//    @SystemMessage("你是是由项目贡献者开发的专注大学生心理健康的智能体," +
//            "请你以专业心理医生的角度回答问题")
//    String chat(@MemoryId String memoryId, @UserMessage String message);

    @SystemMessage("你是是由项目贡献者开发的专注大学生心理健康的智能体,请你以专业心理医生的角度回答问题")
    TokenStream streamChat(@UserMessage String message);

    Flux<String> fluxChat(@UserMessage String prompt);
//    @SystemMessage("你是由项目贡献者开发的专注大学生心理健康的能联网搜索的智能体,请你以专业心理医生的身份回答问题")
//    Flux<String> fluxChat(@MemoryId String memoryId,@UserMessage String prompt);
}
