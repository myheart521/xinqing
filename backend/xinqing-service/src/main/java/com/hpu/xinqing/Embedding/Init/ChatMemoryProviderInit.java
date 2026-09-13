package com.hpu.xinqing.Embedding.Init;

import com.hpu.xinqing.Embedding.rebuild.PersistentChatMemoryStore;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
@RequiredArgsConstructor
public class ChatMemoryProviderInit {

    final PersistentChatMemoryStore chatMemoryStore;

    @Bean
    public ChatMemoryProvider initChatMemory() {
        return memoryId -> MessageWindowChatMemory.builder()
                .id(memoryId)
                .maxMessages(10)
                .chatMemoryStore(chatMemoryStore)
                .build();
    }
}
