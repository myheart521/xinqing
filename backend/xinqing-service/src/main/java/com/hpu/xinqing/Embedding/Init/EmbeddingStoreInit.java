package com.hpu.xinqing.Embedding.Init;

import dev.langchain4j.community.store.embedding.redis.RedisEmbeddingStore;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class EmbeddingStoreInit {

    @Value("${spring.data.redis.host}")
    private String redisHost;

    @Value("${spring.data.redis.port}")
    private int redisPort;

    @Value("${spring.data.redis.password:}")
    private String redisPassword;

    //初始化redis数据库
    @Bean
    @Primary
    public RedisEmbeddingStore initEmbeddingStore() {
        RedisEmbeddingStore.Builder builder = RedisEmbeddingStore.builder()
                .host(redisHost)
                .port(redisPort)
                .user("default")
                .dimension(1024)
                .indexName("rag-index");
        if (redisPassword != null && !redisPassword.isBlank()) {
            builder.password(redisPassword);
        }
        return builder.build();
    }

    //这个是心理预警的RAG

}
