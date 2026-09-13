package com.hpu.xinqing.factory;



import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.Singleton;
import cn.hutool.core.lang.func.Func0;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.RuntimeUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.hpu.xinqingpojo.enmus.AiPlatformEnum;
import dev.langchain4j.model.chat.ChatLanguageModel;
import org.springframework.stereotype.Service;


/**
 * AI Model 模型工厂的实现类
 *
 */
@Service
public class AiModelFactoryImpl implements AiModelFactory {

    @Override
    public ChatLanguageModel getOrCreateChatModel(AiPlatformEnum platform, String apiKey, String model,String url) {
        String cacheKey = buildClientCacheKey(ChatLanguageModel.class, platform, apiKey, url,model);
        return Singleton.get(cacheKey, (Func0<ChatLanguageModel>) () -> {
            // noinspection EnhancedSwitchMigration
            switch (platform) {
                case TONG_YI:
                    return buildTongYiChatModel(apiKey,model);
                case OLLAMA:
                    return buildOllamaChatModel(apiKey,url,model);
                default:
                    throw new IllegalArgumentException(StrUtil.format("未知平台({})", platform));
            }
        });
    }

    private ChatLanguageModel buildOllamaChatModel(String apiKey, String url, String model) {
        return null;
    }

    @Override
    public ChatLanguageModel getDefaultChatModel(AiPlatformEnum platform) {
        return null;
    }


    //生成唯一caCheKey
    private static String buildClientCacheKey(Class<?> clazz, Object... params) {
        if (ArrayUtil.isEmpty(params)) {
            return clazz.getName();
        }
        return StrUtil.format("{}#{}", clazz.getName(), ArrayUtil.join(params, "_"));
    }

    //通义千问的ChatModel
    private ChatLanguageModel buildTongYiChatModel(String apiKey, String model) {
        return null;
    }




}
