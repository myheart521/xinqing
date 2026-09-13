package com.hpu.xinqing.Embedding.Init;


import com.hpu.xinqing.properties.SearchConfig;
import dev.langchain4j.web.search.searchapi.SearchApiWebSearchEngine;
import dev.langchain4j.web.search.tavily.TavilyWebSearchEngine;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class WebSearchInit {

    private final SearchConfig searchConfig;

    //初始化实现联网搜索能力,创建SearchApiWebSearchEngine把搜索引擎与apikey交给大模型
//    @Bean
//    public SearchApiWebSearchEngine initWebSearchEngine() {
//        Map<String, Object> optionalParameters = new HashMap<>();
//        optionalParameters.put("ct", "1");//使用简体中文
//        return SearchApiWebSearchEngine.builder()
//                .engine(searchConfig.getEngine())
//                .apiKey(searchConfig.getApiKey())
////                .optionalParameters(optionalParameters)//用于添加一些搜索的参数
//                .build();
//
//    }
    @Bean
    public TavilyWebSearchEngine initWebSearchEngine() {
        Map<String, Object> optionalParameters = new HashMap<>();
        optionalParameters.put("ct", "1");//使用简体中文
        return TavilyWebSearchEngine.builder()
                .apiKey(searchConfig.getApiKey())
//                .optionalParameters(optionalParameters)//用于添加一些搜索的参数
                .build();

    }

    //


}
