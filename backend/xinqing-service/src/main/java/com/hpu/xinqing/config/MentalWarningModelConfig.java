package com.hpu.xinqing.config;

import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiLanguageModel;
import dev.langchain4j.http.client.jdk.JdkHttpClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class MentalWarningModelConfig {

    @Value("${xinqing.mental-warning.base-url:http://127.0.0.1:48084/v1}")
    private String baseUrl;

    @Value("${xinqing.mental-warning.api-key:}")
    private String apiKey;

    @Value("${xinqing.mental-warning.model:psych-qwen2.5-3b-lora}")
    private String modelName;

    @Value("${xinqing.mental-warning.temperature:0.2}")
    private Double temperature;

    @Value("${xinqing.mental-warning.timeout-seconds:120}")
    private Long timeoutSeconds;

    @Value("${xinqing.mental-warning.max-retries:2}")
    private Integer maxRetries;

    @Value("${xinqing.mental-warning.max-tokens:2048}")
    private Integer maxTokens;

    @Bean("mentalWarningChatModel")
    public OpenAiChatModel mentalWarningChatModel() {
        return OpenAiChatModel.builder()
                .baseUrl(normalizeBaseUrl(baseUrl))
                .apiKey(apiKey)
                .modelName(modelName)
                .httpClientBuilder(newHttpClientBuilder())
                .temperature(temperature)
                .maxTokens(maxTokens)
                .timeout(Duration.ofSeconds(timeoutSeconds))
                .maxRetries(maxRetries)
                .build();
    }

    @Bean("mentalWarningLanguageModel")
    public OpenAiLanguageModel mentalWarningLanguageModel() {
        return OpenAiLanguageModel.builder()
                .baseUrl(normalizeBaseUrl(baseUrl))
                .apiKey(apiKey)
                .modelName(modelName)
                .httpClientBuilder(newHttpClientBuilder())
                .temperature(temperature)
                .timeout(Duration.ofSeconds(timeoutSeconds))
                .maxRetries(maxRetries)
                .build();
    }

    private JdkHttpClientBuilder newHttpClientBuilder() {
        return new JdkHttpClientBuilder()
                .connectTimeout(Duration.ofSeconds(timeoutSeconds))
                .readTimeout(Duration.ofSeconds(timeoutSeconds));
    }

    private String normalizeBaseUrl(String value) {
        String trimmed = value == null ? "" : value.trim();
        if (trimmed.isBlank()) {
            return "http://127.0.0.1:48084/v1";
        }
        if (trimmed.endsWith("/v1")) {
            return trimmed;
        }
        if (trimmed.endsWith("/v1/")) {
            return trimmed.substring(0, trimmed.length() - 1);
        }
        if (trimmed.endsWith("/")) {
            return trimmed + "v1";
        }
        return trimmed + "/v1";
    }
}
