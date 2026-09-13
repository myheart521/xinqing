package com.hpu.xinqing.Embedding.rebuild;

import cn.hutool.core.lang.hash.Hash;
import com.hpu.xinqing.service.ai.IAiChatMessageService;
import com.hpu.xinqingcommon.constant.RedisConstant;
import dev.langchain4j.data.message.*;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class PersistentChatMemoryStore implements ChatMemoryStore {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private IAiChatMessageService chatMessageService;
    //使用redis对对话数据进行存储更改
    @Override
    public List<ChatMessage> getMessages(Object memoryId) {
        // 实现根据memoryId从持久化存储中获取所有消息。
        // 可以使用ChatMessageDeserializer.messageFromJson(String)和
        // ChatMessageDeserializer.messagesFromJson(String)辅助方法将聊天消息从JSON反序列化。
        String message = stringRedisTemplate.opsForValue().get(memoryId);
        List<ChatMessage> list = ChatMessageDeserializer.messagesFromJson(message);
        return list;

    }

    @Override
    public void updateMessages(Object memoryId, List<ChatMessage> messages) {
        // 实现根据memoryId更新持久化存储中的所有消息。
        // 可以使用ChatMessageSerializer.messageToJson(ChatMessage)和
        // ChatMessageSerializer.messagesToJson(List<ChatMessage>)辅助方法将聊天消息序列化为JSON。
        String str = ChatMessageSerializer.messagesToJson(messages);
        //设置数据保留时间为24小时
        stringRedisTemplate.opsForValue().set((String) memoryId,str,RedisConstant.MEMORY_TIME, TimeUnit.HOURS);
        ChatMessage chatMessage = messages.get(messages.size() - 1);
        chatMessageService.insertMessage(memoryId,chatMessage);
    }

    @Override
    public void deleteMessages(Object memoryId) {
        // 实现根据memoryId删除持久化存储中的所有消息。
        stringRedisTemplate.delete((String) memoryId);
    }

}
