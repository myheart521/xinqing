package com.hpu.xinqing.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsUtils;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * WebFlux相关配置但不启用@EnableWebFlux，避免与WebMVC冲突
 */
@Configuration
public class WebFluxConfig {

    /**
     * 全局CORS配置
     */
    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        // 允许所有来源
        corsConfig.addAllowedOrigin("*");
        // 允许所有请求头
        corsConfig.addAllowedHeader("*");
        // 允许所有方法
        corsConfig.addAllowedMethod("*");
        // 暴露响应头
        corsConfig.addExposedHeader(HttpHeaders.CONTENT_DISPOSITION);
        // 允许凭证
        corsConfig.setAllowCredentials(true);
        // 设置缓存时间
        corsConfig.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // 对所有路径应用这些CORS配置
        source.registerCorsConfiguration("/**", corsConfig);

        return new CorsWebFilter(source);
    }

    /**
     * SSE特定的CORS配置
     */
    @Bean
    public WebFilter sseHeadersFilter() {
        return (ServerWebExchange exchange, WebFilterChain chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();

            // 检查是否SSE请求路径
            if (request.getURI().getPath().contains("/sse/")) {
                // 设置SSE相关响应头
                response.getHeaders().add(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, max-age=0, must-revalidate");
                response.getHeaders().add(HttpHeaders.PRAGMA, "no-cache");
                response.getHeaders().add(HttpHeaders.EXPIRES, "0");
                
                // 如果是预检请求，直接返回成功
                if (CorsUtils.isPreFlightRequest(request)) {
                    response.setStatusCode(HttpStatus.OK);
                    return Mono.empty();
                }
            }

            return chain.filter(exchange);
        };
    }
} 