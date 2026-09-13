package com.hpu.xinqing.Embedding.Init;

import dev.langchain4j.community.store.embedding.redis.RedisEmbeddingStore;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class EmbeddingStoreInit {

    //初始化redis数据库
    @Bean
    @Primary
    public RedisEmbeddingStore initEmbeddingStore() {
        return RedisEmbeddingStore.builder()
                .host(System.getenv("SPRING_DATA_REDIS_HOST"))
                .port(6379)
                .user("default")
                .password(System.getenv("SPRING_DATA_REDIS_PASSWORD"))
                .dimension(1024)
                .indexName("rag-index")
                .build();
    }

    //这个是心理预警的RAG

}
