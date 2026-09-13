package com.hpu.xinqing.utils.mq.u2uChat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.hpu.xinqingpojo.DTO.Msg;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class Producer {
    @Autowired
    RabbitTemplate rabbitTemplate;
    @Autowired
    ObjectMapper objectMapper;

    public void sendMessage(Msg msg) {
        log.info("msg.constructRouterKey()   " + msg.constructRouterKey());
        try {
            rabbitTemplate.convertAndSend("u2uChat-exchange", msg.constructRouterKey(), objectMapper.writeValueAsString(msg));
            System.out.println("objectMapper.writeValueAsString(msg) = " + objectMapper.writeValueAsString(msg));
        } catch (JsonProcessingException e) {
            log.error("JSON转换失败", e);
        }
    }
}
