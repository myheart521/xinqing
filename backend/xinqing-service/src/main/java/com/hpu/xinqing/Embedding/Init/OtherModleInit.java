package com.hpu.xinqing.Embedding.Init;


import dev.langchain4j.http.client.jdk.JdkHttpClientBuilder;
import dev.langchain4j.http.client.spring.restclient.SpringRestClient;
import dev.langchain4j.http.client.spring.restclient.SpringRestClientBuilder;
import dev.langchain4j.model.ollama.OllamaStreamingChatModel;
import dev.langchain4j.model.ollama.OllamaStreamingLanguageModel;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.VirtualThreadTaskExecutor;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;


@Configuration
@RequiredArgsConstructor
public class OtherModleInit {

    /**
     *     baseUrl: http://localhost:11434
     *     model: qwen2.5:latest
     * @return
     */

    @Bean
    public OllamaStreamingChatModel initOllamaModle() {

        return OllamaStreamingChatModel.builder()
                .httpClientBuilder(new JdkHttpClientBuilder())
                .baseUrl("http://localhost:11434")
                .modelName("qwen2.5")
                .logRequests(true)
                .logRequests(true)
                .build();
    }
}
