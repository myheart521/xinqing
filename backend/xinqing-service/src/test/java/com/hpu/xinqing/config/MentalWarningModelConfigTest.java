package com.hpu.xinqing.config;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class MentalWarningModelConfigTest {

    @Test
    void shouldBuildMentalWarningModelsWithExplicitHttpClient() {
        MentalWarningModelConfig config = new MentalWarningModelConfig();
        ReflectionTestUtils.setField(config, "baseUrl", "http://127.0.0.1:48084/v1");
        ReflectionTestUtils.setField(config, "apiKey", "");
        ReflectionTestUtils.setField(config, "modelName", "psych-qwen2.5-3b-lora");
        ReflectionTestUtils.setField(config, "temperature", 0.2D);
        ReflectionTestUtils.setField(config, "timeoutSeconds", 5L);
        ReflectionTestUtils.setField(config, "maxRetries", 1);
        ReflectionTestUtils.setField(config, "maxTokens", 128);

        assertNotNull(assertDoesNotThrow(config::mentalWarningChatModel));
        assertNotNull(assertDoesNotThrow(config::mentalWarningLanguageModel));
    }
}
