package com.hpu.xinqing.Embedding.Init;

import dev.langchain4j.community.model.dashscope.QwenEmbeddingModel;
import dev.langchain4j.community.store.embedding.redis.RedisEmbeddingStore;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class EmbeddingStoreContentRetrieverInit {

    @Autowired
    private RedisEmbeddingStore embeddingStore;
    @Autowired
    private QwenEmbeddingModel embeddingModel;

    @Bean
    @Primary
    public EmbeddingStoreContentRetriever initEmbeddingStoreContentRetriever() {

        return EmbeddingStoreContentRetriever.builder()
                .embeddingModel(embeddingModel)
                .embeddingStore(embeddingStore)
                .maxResults(10)
                .minScore(0.7)
                .build();
    }
}
