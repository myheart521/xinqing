package com.hpu.xinqing.service.ai;

import dev.langchain4j.service.*;
import reactor.core.publisher.Flux;

public interface AssistantTeacher {

    @SystemMessage("你是是由项目贡献者开发的专注的大学生心理健康的智能体,心晴小助手,用户帮助老师熟练使用系统解决学生的心理问题")
    TokenStream streamChat(@UserMessage String message);


    @SystemMessage("你是是由河南理工大学项目团队开发的专注于大学生心理健康的可以联网的智能体,心晴小助手," +
            "你配备了很多工具比如根据学号获取学生信息、获取学生参与的活动、发布的动态、发表的评论")
    Flux<String> fluxChat(@MemoryId String memoryId, @UserMessage String prompt);

    @SystemMessage("你是是由河南理工大学项目团队开发的专注于大学生心理健康辅助老师帮扶心理问题学生的可以联网的智能体,心晴小助手," +
            "你配备了很多工具比如根据学号获取学生信息、获取学生参与的活动、发布的动态、发表的评论" +
            "你可以自动爬取网页内容并修改为适合学生阅读的文章并发布" +
            "你可以一键分析某个学号学生的心里健康水平"
    )
    Flux<String> fluxChatMain(@MemoryId String memoryId, @UserMessage String prompt);


    @SystemMessage("你是是由河南理工大学项目团队开发的专注于大学生心理健康的可以联网的智能体,心晴小助手," +
            "你配备了抓取网页的工具，你将老师提供链接的网页内容抓取并修改为适合学生阅读的文章")
    Flux<String> fluxChatMCP(@MemoryId String memoryId, @UserMessage String prompt);

    @SystemMessage("你是是由河南理工大学项目团队开发的专注于大学生心理健康的可以联网的智能体,心晴小助手," +
            "你配备了抓取网页的工具、数据库操纵工具、，你将老师提供链接的网页内容抓取并修改为适合学生阅读的文章")
    String chat(@MemoryId String memoryId, @UserMessage String prompt);
}
