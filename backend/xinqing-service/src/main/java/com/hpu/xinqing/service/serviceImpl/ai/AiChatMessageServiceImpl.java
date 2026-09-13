package com.hpu.xinqing.service.serviceImpl.ai;


import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hpu.xinqing.mapper.AiChatMessageMapper;
import com.hpu.xinqing.service.ai.IAiChatMemoryService;
import com.hpu.xinqing.service.ai.IAiChatMessageService;
import com.hpu.xinqingcommon.constant.SystemConstants;
import com.hpu.xinqingpojo.DTO.ChatWithAIAnalyseDTO;
import com.hpu.xinqingpojo.entity.AiChatMemory;
import com.hpu.xinqingpojo.entity.AiChatMessage;
import dev.langchain4j.data.message.*;
import jakarta.annotation.Resource;
import lombok.Locked;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <p>
 * AI 聊天消息表 服务实现类
 * </p>
 *
 * @since 2025-03-19
 */
@Service
@Slf4j
public class AiChatMessageServiceImpl extends ServiceImpl<AiChatMessageMapper, AiChatMessage> implements IAiChatMessageService {

    private static final String TOOL_EXECUTION_RESULT_TYPE = ChatMessageType.TOOL_EXECUTION_RESULT.name();

    @Resource
    private IAiChatMemoryService aiChatMemoryService;

    public static boolean isDisplayableMessageType(String type) {
        return !TOOL_EXECUTION_RESULT_TYPE.equals(type);
    }

    public static List<AiChatMessage> filterDisplayableMessages(List<AiChatMessage> messages) {
        if (messages == null || messages.isEmpty()) {
            return List.of();
        }
        return messages.stream()
                .filter(message -> message != null && isDisplayableMessageType(message.getType()))
                .toList();
    }

    @Transactional
    @Override
    public void insertMessage(Object memoryId, ChatMessage chatMessage) {
        ChatMessageType type = chatMessage.type();
        String name = type.name();
        AiChatMessage aiChatMessage = new AiChatMessage();
        if(name.equals("USER")){
            UserMessage userMessage=(UserMessage) chatMessage;
            String text = userMessage.singleText();
            String[] split = text.split("\n\nAnswer using the following information:");
            text=split[0];
            aiChatMessage.setContent(text);
        } else if (name.equals("AI")) {
            AiMessage aiMessage=(AiMessage) chatMessage;
            String text = aiMessage.text();
            aiChatMessage.setContent(text);
        }else if (name.equals(TOOL_EXECUTION_RESULT_TYPE)) {
            log.debug("Tool execution result is internal and will not be saved as a display message, memoryId={}", memoryId);
            return;
        } else {
            log.info("其他信息，不存入数据库");
            return;
        }
        aiChatMessage.setType(name);
        aiChatMessage.setMemoryId((String) memoryId);
        save(aiChatMessage);
    }

    @Override
    public List<AiChatMessage> selectByMemoryId(String memoryId, LocalDateTime startTime) {

        Page<AiChatMessage> page = lambdaQuery().eq(AiChatMessage::getMemoryId, memoryId)
                .eq(AiChatMessage::getDeleted, false)
                .ne(AiChatMessage::getType, TOOL_EXECUTION_RESULT_TYPE)
                .lt(AiChatMessage::getCreateTime, startTime)//小于
                .orderByDesc(AiChatMessage::getCreateTime)//倒序
                .page(new Page<>(1, SystemConstants.AI_PAGE_SIZE));
        List<AiChatMessage> list =page.getRecords();
        Collections.reverse(list);
        return filterDisplayableMessages(list);
    }

    @Override
    public List<ChatWithAIAnalyseDTO> selectAnalyseByUserId(Long userId) {
        List<AiChatMemory> memoryList = aiChatMemoryService.getByOtherUserId(userId);
        if(memoryList.isEmpty()){
            return List.of();
        }
        LocalDateTime endTime= LocalDateTime.now();
        LocalDateTime startTime = endTime.minusWeeks(2);
        List<ChatWithAIAnalyseDTO> chatWithAIAnalyseDTOList = new ArrayList<>();
        memoryList.stream().forEach(memory -> {
            List<AiChatMessage> list = lambdaQuery().eq(AiChatMessage::getMemoryId, memory.getId())
                    .between(AiChatMessage::getCreateTime, startTime, endTime)
                    .eq(AiChatMessage::getType,"USER")
                    .eq(AiChatMessage::getDeleted, false)
                    .orderByDesc(AiChatMessage::getCreateTime).list();
            if(!list.isEmpty()){
                List<ChatWithAIAnalyseDTO> chatList = BeanUtil.copyToList(list, ChatWithAIAnalyseDTO.class);
                chatWithAIAnalyseDTOList.addAll(chatList);
            }
        });
        return chatWithAIAnalyseDTOList;
    }
}
