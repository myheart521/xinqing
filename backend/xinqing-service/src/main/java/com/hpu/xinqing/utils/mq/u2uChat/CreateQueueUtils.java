package com.hpu.xinqing.utils.mq.u2uChat;


import com.hpu.xinqingpojo.DTO.Msg;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Properties;

@Component
@Slf4j
public class CreateQueueUtils {

    @Resource(name = "u2uChatExchange")
    DirectExchange directExchange;
    @Autowired
    RabbitAdmin rabbitAdmin;

    public void bindU2UQueue(Long receiverId){
        Msg msg = new Msg(receiverId);
        String queueName=msg.constructRouterKey();
        //判断队列是否存在
        if (hasQueue(queueName)){
            log.info("这个队列存在, 就不再重复创建了：u2u-chat-user-"+receiverId);
            return;
        }
        //声明队列(如果3天内没有收到新的信息那么这个队列会被删除
        Queue queue = new Queue(queueName, true, false, false, Map.of("x-expires", 3 * 24 * 60 * 60 * 1000));
        rabbitAdmin.declareQueue(queue);
        //声明绑定关系
        Binding binding = BindingBuilder.bind(queue).to(directExchange).with(msg.constructRouterKey());
        rabbitAdmin.declareBinding(binding);
        log.info("成功创建队列并绑定到DirExchange：u2u-chat-user-"+receiverId+"\n他的routerKey是："+msg.constructRouterKey());
    }

    public boolean hasQueue(String queueName){
        return rabbitAdmin.getQueueProperties(queueName)!=null;
    }


}
