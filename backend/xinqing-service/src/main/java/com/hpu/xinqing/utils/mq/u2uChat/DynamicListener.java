package com.hpu.xinqing.utils.mq.u2uChat;


import com.hpu.xinqing.mapper.ChatMapper;
import com.hpu.xinqing.utils.common.JSONUtil;
import com.hpu.xinqingpojo.DTO.Msg;
import com.hpu.xinqingpojo.entity.Chat;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;



@Component
@Slf4j
public class DynamicListener {
    @Autowired
    ConnectionFactory connectionFactory;

    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    JSONUtil jsonUtil;

    @Resource
    ChatMapper chatMapper;
    @Resource(name = "ioPool")
    TaskExecutor executor;




    /**
     * 为已经存在的队列创建一个监听
     * @param queueName
     */
    public void addListener(String queueName){
        SimpleMessageListenerContainer container = initContainer();
        container.setQueueNames(queueName);
        log.info("为队列{}创建监听", queueName);
        container.setMessageListener(message -> {
            Msg msg = jsonUtil.parseMsg(message.getBody());
            log.info("信息为"+msg);
            //存入数据库
            executor.execute(() -> chatMapper.insert(Chat.insertChat(msg.getSenderId(), msg.getReceiverId(), msg.getContent())));
        });
        container.start();
    }
    private SimpleMessageListenerContainer initContainer(){
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        return container;
    }
}
