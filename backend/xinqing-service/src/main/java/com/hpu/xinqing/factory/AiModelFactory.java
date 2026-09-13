package com.hpu.xinqing.factory;

import com.hpu.xinqingpojo.enmus.AiPlatformEnum;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.image.ImageModel;

/**
 * AI Model 模型工厂的接口类
 *
 */
public interface AiModelFactory {

    /**
     * 基于指定配置，获得 ChatModel 对象
     * 如果不存在，则进行创建
     *
     * @param platform 平台
     * @param apiKey API KEY
     * @param url API URL
     * @return ChatModel 对象
     */
    ChatLanguageModel getOrCreateChatModel(AiPlatformEnum platform, String apiKey, String model,String url);

    /**
     * 基于默认配置，获得 ChatModel 对象
     *
     * 默认配置，指的是在 application.yaml 配置文件中的 spring.ai 相关的配置
     *
     * @param platform 平台
     * @return ChatModel 对象
     */
    ChatLanguageModel getDefaultChatModel(AiPlatformEnum platform);


}
