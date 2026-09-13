package com.hpu.xinqing.service.serviceImpl.ai;

import com.hpu.xinqingpojo.entity.AiChatMessage;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AiChatMessageServiceImplTest {

    @Test
    void toolExecutionResultIsInternalAndShouldNotBeDisplayed() {
        assertFalse(AiChatMessageServiceImpl.isDisplayableMessageType("TOOL_EXECUTION_RESULT"));
        assertTrue(AiChatMessageServiceImpl.isDisplayableMessageType("USER"));
        assertTrue(AiChatMessageServiceImpl.isDisplayableMessageType("AI"));
    }

    @Test
    void filtersInternalToolMessagesFromHistory() {
        AiChatMessage user = message("USER", "查博客");
        AiChatMessage tool = message("TOOL_EXECUTION_RESULT", "共找到 3 条记录");
        AiChatMessage ai = message("AI", "以下是最近 3 条博客动态");

        List<AiChatMessage> filtered = AiChatMessageServiceImpl.filterDisplayableMessages(List.of(user, tool, ai));

        assertEquals(List.of(user, ai), filtered);
    }

    private AiChatMessage message(String type, String content) {
        AiChatMessage message = new AiChatMessage();
        message.setType(type);
        message.setContent(content);
        return message;
    }
}
