package com.hpu.xinqing.service.ai;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import reactor.core.publisher.Flux;

public interface AssistantTeacherMCP {

    @SystemMessage("你是是由河南理工大学项目团队开发的专注于大学生心理健康的可以联网的智能体,心晴小助手," +
            "你配备了抓取网页的工具，你将老师提供链接的网页内容抓取并修改为适合学生阅读的文章")
    Flux<String> fluxChatMCP(@MemoryId String memoryId, @UserMessage String prompt);

    String chat(@MemoryId String memoryId, @UserMessage String prompt);

}
