package com.hpu.xinqingpojo.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.stereotype.Component;

/**
 * 仅供RabbitMQ内部传输信息可用
 */
@Data
@NoArgsConstructor
@ToString
@Component
public class Msg {

    private static String u2uPrefix="u2u-chat-user-";

    private long senderId;

    private long receiverId;

    private String content;

    public Msg(Long receiverId){
        this.receiverId=receiverId;
    }

    public Msg(Long senderId,Long receiverId){
        this.senderId=senderId;
        this.receiverId=receiverId;
    }

    public Msg(Long senderId,Long receiverId,String content){
        this.senderId=senderId;
        this.receiverId=receiverId;
        this.content=content;
    }
    public static String constructRouterKey(Long senderId,Long receiverId){
        return u2uPrefix+receiverId;
    }
    public String constructRouterKey(){
        return u2uPrefix+receiverId;
    }

}