package com.hpu.xinqing.utils.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.hpu.xinqingpojo.DTO.Msg;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
@Slf4j
public class JSONUtil {
    @Autowired
    ObjectMapper objectMapper;

    public Msg parseMsg(byte[] body){
        String jsonContent = null;
        try {
            String rawMessage = new String(body, StandardCharsets.UTF_8);
            // 如果消息本身是嵌套的 JSON 字符串，先解包
            jsonContent = objectMapper.readValue(rawMessage, String.class);
            // 将解包后的 JSON 字符串解析为目标类
            return objectMapper.readValue(jsonContent, Msg.class);
        } catch (JsonProcessingException e) {
            log.error("将该字符串转为Msg对象失败:{}", jsonContent);
            throw new RuntimeException(e);
        }
    }

    public <T> T parseToObj(byte[] body,Class<T> clazz){
        String jsonContent = null;
        try {
            String rawMessage = new String(body, StandardCharsets.UTF_8);
            // 如果消息本身是嵌套的 JSON 字符串，先解包
            jsonContent = objectMapper.readValue(rawMessage, String.class);
            // 将解包后的 JSON 字符串解析为目标类
            return objectMapper.readValue(jsonContent, clazz);
        } catch (JsonProcessingException e) {
            log.error("将该字符串转为Msg对象失败:{}", jsonContent);
            throw new RuntimeException(e);
        }
    }
}