package com.hpu.xinqing.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@Configuration
public class RabbitMQConfig {

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter(Jackson2ObjectMapperBuilder.json().build());
    }

//    @Bean
//    public RabbitListenerContainerFactory<?> rabbitListenerContainerFactory(SimpleRabbitListenerContainerFactory factory) {
//        factory.setMessageConverter(messageConverter());
//        return factory;
//    }


    @Bean(name = "u2uChatExchange")
    public DirectExchange directExchange() {
        return new DirectExchange("u2uChat-exchange");
    }




    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        System.out.println(connectionFactory);
        return new RabbitAdmin(connectionFactory);
    }

}

