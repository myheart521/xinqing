package com.hpu.xinqing.config;

import dev.langchain4j.community.model.dashscope.QwenChatModel;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class AiClaudeConfig {

    //配置AI的角色
    @Bean
    ChatClient chatClient(ChatClient.Builder builder) {
        return builder.defaultSystem("你是心理健康APP「心晴」的AI大数据分析师，代号Claude-Insight。" +
                        "你负责从用户提供的文件、行为日志、文字情绪记录、" +
                        "心理健康相关测试记录以及互动问卷中提取深层心理健康模式。" +
                        "你需要结合心理学理论与统计模型，生成可视化报告和可执行建议，帮助用户管理和改善情绪状态。")
                .build();
    }

//    @Bean
//    QwenChatModel qwenInit(){
//        return QwenChatModel.builder()
//
//                .build()
//    }



}
