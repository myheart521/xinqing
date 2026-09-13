package com.hpu.xinqing.websocket;

import com.hpu.xinqing.mapper.ChatMapper;
import com.hpu.xinqingpojo.entity.Chat;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class U2UChatTest {

    @AfterEach
    void tearDown() {
        U2UChat.sessionMap.clear();
    }

    @Test
    void handleTextMessageShouldDeliverToOnlineReceiverAndPersistDirectly() throws Exception {
        U2UChat chat = new U2UChat();
        ChatMapper chatMapper = mock(ChatMapper.class);
        ReflectionTestUtils.setField(chat, "chatMapper", chatMapper);

        WebSocketSession senderSession = mock(WebSocketSession.class);
        WebSocketSession receiverSession = mock(WebSocketSession.class);
        when(senderSession.getAttributes()).thenReturn(Map.of(
                "senderId", 14L,
                "receiverId", 2L
        ));
        when(receiverSession.isOpen()).thenReturn(true);
        U2UChat.sessionMap.put(2L, receiverSession);

        chat.handleTextMessage(senderSession, new TextMessage("hello"));

        verify(receiverSession).sendMessage(argThat(message -> "hello".equals(message.getPayload())));
        verify(chatMapper).insert(argThat((Chat saved) ->
                saved.getUserSenderId().equals(14L)
                        && saved.getUserReceiverId().equals(2L)
                        && "hello".equals(saved.getContent())
                        && saved.getCreateTime() != null
        ));
    }
}
