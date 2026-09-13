package com.hpu.xinqing.utils.sse;

import com.hpu.xinqing.utils.sse.enums.DataType;
import com.hpu.xinqing.utils.sse.model.SseMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * SSE连接管理器
 * 支持WebFlux和MVC两种风格的SSE连接
 */
@Component
public class SseManager {
    private static final Logger log = LoggerFactory.getLogger(SseManager.class);

    // 默认超时时间：1小时
    private static final long DEFAULT_TIMEOUT = TimeUnit.HOURS.toMillis(1);

    // WebFlux风格的SSE连接映射，使用Sinks进行多播
    private final Map<Long, Sinks.Many<ServerSentEvent<Object>>> sinks = new ConcurrentHashMap<>();

    // MVC风格的SSE连接映射
    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    /**
     * 创建WebFlux风格的SSE连接
     *
     * @param userId 用户ID
     * @return SSE事件流
     */
    public Flux<ServerSentEvent<Object>> createConnection(Long userId) {
        log.info("创建用户 {} 的SSE连接 (WebFlux风格)", userId);

        // 检查是否已存在连接，如有则关闭旧连接
        closeExistingConnection(userId);

        // 创建新的Sink
        Sinks.Many<ServerSentEvent<Object>> sink = Sinks.many().multicast().onBackpressureBuffer();
        sinks.put(userId, sink);

        // 发送连接成功事件
        SseMessage connectMsg = new SseMessage(DataType.CONNECT, "连接成功");
        sink.tryEmitNext(ServerSentEvent.builder()
                .id(connectMsg.getId())
                .event(DataType.CONNECT.name())
                .data(connectMsg.getData())
                .build());

        log.info("用户 {} 的SSE连接已创建，当前连接数: {}", userId, sinks.size());

        // 创建返回的Flux，添加完成和取消处理
        return sink.asFlux()
                .doOnCancel(() -> {
                    log.info("用户 {} 的SSE连接已取消", userId);
                    closeConnection(userId);
                })
                .doOnComplete(() -> {
                    log.info("用户 {} 的SSE连接已完成", userId);
                    closeConnection(userId);
                })
                .doOnError(error -> {
                    log.error("用户 {} 的SSE连接发生错误: {}", userId, error.getMessage(), error);
                    closeConnection(userId);
                });
    }

    /**
     * 创建MVC风格的SSE连接
     *
     * @param userId 用户ID
     * @return SseEmitter实例
     */
    public SseEmitter createMvcConnection(Long userId) {
        log.info("创建用户 {} 的SSE连接 (MVC风格)", userId);

        // 检查是否已存在连接，如有则关闭旧连接
        closeExistingConnection(userId);

        // 创建新的SseEmitter
        SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);
        emitters.put(userId, emitter);

        // 设置完成、超时和错误回调
        emitter.onCompletion(() -> {
            log.info("用户 {} 的SSE连接已完成 (MVC风格)", userId);
            emitters.remove(userId);
        });

        emitter.onTimeout(() -> {
            log.info("用户 {} 的SSE连接已超时 (MVC风格)", userId);
            emitters.remove(userId);
        });

        emitter.onError(error -> {
            log.error("用户 {} 的SSE连接发生错误 (MVC风格): {}", userId, error.getMessage(), error);
            emitters.remove(userId);
        });

        // 发送连接成功事件
        try {
            emitter.send(SseEmitter.event()
                    .id(UUID.randomUUID().toString())
                    .name(DataType.CONNECT.name())
                    .data("连接成功", MediaType.TEXT_PLAIN));

            log.info("已向用户 {} 发送初始连接事件", userId);
        } catch (IOException e) {
            log.error("向用户 {} 发送初始连接事件失败: {}", userId, e.getMessage(), e);
            emitter.completeWithError(e);
        }

        return emitter;
    }

    /**
     * 推送SSE事件
     *
     * @param userId  用户ID
     * @param message SSE消息
     * @return 是否推送成功
     */
    public boolean pushEvent(Long userId, SseMessage message) {
        log.info("向用户 {} 推送 {} 类型的事件: {}", userId, message.getEventType(), message.getId());

        boolean success = false;

        // 尝试使用WebFlux方式推送
        if (sinks.containsKey(userId)) {
            try {
                ServerSentEvent<Object> event = ServerSentEvent.builder()
                        .id(message.getId())
                        .event(message.getEventType().name())
                        .data(message.getData())
                        .build();

                Sinks.EmitResult result = sinks.get(userId).tryEmitNext(event);
                success = result.isSuccess();

                if (!success) {
                    log.warn("向用户 {} 推送WebFlux事件失败: {}", userId, result);
                } else {
                    log.debug("成功向用户 {} 推送WebFlux事件", userId);
                }
            } catch (Exception e) {
                log.error("向用户 {} 推送WebFlux事件时发生异常: {}", userId, e.getMessage(), e);
            }
        }

        // 尝试使用MVC方式推送
        if (!success && emitters.containsKey(userId)) {
            try {
                SseEmitter emitter = emitters.get(userId);
                emitter.send(SseEmitter.event()
                        .id(message.getId())
                        .name(message.getEventType().name())
                        .data(message.getData(), MediaType.APPLICATION_JSON));

                success = true;
                log.debug("成功向用户 {} 推送MVC事件", userId);
            } catch (IOException e) {
                log.error("向用户 {} 推送MVC事件时发生IO异常: {}", userId, e.getMessage(), e);
                emitters.remove(userId);
            } catch (Exception e) {
                log.error("向用户 {} 推送MVC事件时发生异常: {}", userId, e.getMessage(), e);
            }
        }

        if (!success) {
            log.warn("向用户 {} 推送事件失败，可能没有活跃连接", userId);
        }

        return success;
    }

    /**
     * 检查用户是否有活跃的SSE连接
     *
     * @param userId 用户ID
     * @return 是否有活跃连接
     */
    public boolean hasActiveConnection(Long userId) {
        boolean hasWebFluxConnection = sinks.containsKey(userId);
        boolean hasMvcConnection = emitters.containsKey(userId);

        log.debug("用户 {} 的连接状态: WebFlux={}, MVC={}", userId, hasWebFluxConnection, hasMvcConnection);

        return hasWebFluxConnection || hasMvcConnection;
    }

    /**
     * 关闭用户的SSE连接
     *
     * @param userId 用户ID
     */
    public void closeConnection(Long userId) {
        log.info("关闭用户 {} 的SSE连接", userId);

        // 关闭WebFlux连接
        if (sinks.containsKey(userId)) {
            try {
                Sinks.Many<ServerSentEvent<Object>> sink = sinks.get(userId);
                sink.tryEmitComplete();
                sinks.remove(userId);
                log.debug("已关闭用户 {} 的WebFlux SSE连接", userId);
            } catch (Exception e) {
                log.error("关闭用户 {} 的WebFlux SSE连接时发生错误: {}", userId, e.getMessage(), e);
            }
        }

        // 关闭MVC连接
        if (emitters.containsKey(userId)) {
            try {
                SseEmitter emitter = emitters.get(userId);
                emitter.complete();
                emitters.remove(userId);
                log.debug("已关闭用户 {} 的MVC SSE连接", userId);
            } catch (Exception e) {
                log.error("关闭用户 {} 的MVC SSE连接时发生错误: {}", userId, e.getMessage(), e);
            }
        }
    }

    /**
     * 关闭现有连接（在创建新连接前清理）
     *
     * @param userId 用户ID
     */
    private void closeExistingConnection(Long userId) {
        if (hasActiveConnection(userId)) {
            log.info("用户 {} 已有现有连接，先关闭旧连接", userId);
            closeConnection(userId);
        }
    }
}
