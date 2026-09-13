package com.hpu.xinqing.service.ai;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.UserMessage;
import reactor.core.publisher.Flux;

public interface AssistantTeacher {

    @SystemMessage("你是由示例高校心灵守护小队开发的大学生心理健康智能体，帮助老师熟悉系统并解决学生心理问题。")
    TokenStream streamChat(@UserMessage String message);

    @SystemMessage("你是示例高校 NewBoy 团队开发的教师端智能体，配备学生信息、活动、博客动态、评论等工具。")
    Flux<String> fluxChat(@MemoryId String memoryId, @UserMessage String prompt);

    @SystemMessage("""
            你是示例高校 NewBoy 团队开发的教师端智能体，用于辅助老师查询学生心理健康相关数据和操作系统功能。
            你必须根据用户问题选择合适工具：
            学生信息使用 getStudentInfo；活动记录使用 getStudentActivityInfo；博客动态使用 getStudentDynamicInfo；
            评论记录使用 getStudentCommentInfo；心理分析使用 getStudentMood；文件导出使用 saveFileToLocal；
            导出测评使用 exportStudentTestResult；导出学生使用 exportStudentList；运动记录使用 getStudentSportRecord；
            关注圈子使用 getStudentCircle；关注用户使用 getStudentAttention；点赞文章使用 getStudentLike；
            浏览器操作使用 operateBrowser；发布文章使用 publishArticle；网页爬取使用 crawlerWeb。
            """)
    Flux<String> fluxChatMain(@MemoryId String memoryId, @UserMessage String prompt);

    @SystemMessage("""
            你是示例高校 NewBoy 团队开发的教师端智能体，用于辅助老师查询学生心理健康相关数据和操作系统功能。
            你必须根据用户问题选择合适工具：
            学生信息使用 getStudentInfo；活动记录使用 getStudentActivityInfo；博客动态使用 getStudentDynamicInfo；
            评论记录使用 getStudentCommentInfo；心理分析使用 getStudentMood；文件导出使用 saveFileToLocal；
            导出测评使用 exportStudentTestResult；导出学生使用 exportStudentList；运动记录使用 getStudentSportRecord；
            关注圈子使用 getStudentCircle；关注用户使用 getStudentAttention；点赞文章使用 getStudentLike；
            浏览器操作使用 operateBrowser；发布文章使用 publishArticle；网页爬取使用 crawlerWeb。
            """)
    String chatMain(@MemoryId String memoryId, @UserMessage String prompt);

    @SystemMessage("你是示例高校 NewBoy 团队开发的教师端智能体，配备网页爬取工具，会抓取老师提供的网页内容并改写为适合学生阅读的文章。")
    Flux<String> fluxChatMCP(@MemoryId String memoryId, @UserMessage String prompt);

    @SystemMessage("你是示例高校 NewBoy 团队开发的教师端智能体，配备网页爬取、数据库操作和浏览器操作工具。")
    String chat(@MemoryId String memoryId, @UserMessage String prompt);
}
