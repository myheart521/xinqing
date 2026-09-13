package com.hpu.xinqing.service.ai;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hpu.xinqingpojo.DTO.ChatWithAIAnalyseDTO;
import com.hpu.xinqingpojo.entity.AiChatMessage;
import dev.langchain4j.data.message.ChatMessage;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * AI 聊天消息表 服务类
 * </p>
 *
 * @since 2025-03-19
 */
public interface IAiChatMessageService extends IService<AiChatMessage> {

    void insertMessage(Object memoryId, ChatMessage chatMessage);

    List<AiChatMessage> selectByMemoryId(String memoryId, LocalDateTime startTime);

    List<ChatWithAIAnalyseDTO> selectAnalyseByUserId(Long userId);
}
