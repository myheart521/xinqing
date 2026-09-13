package com.hpu.xinqingpojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @TableName chat
 */
@TableName(value ="chat")
@Data
@NoArgsConstructor
public class Chat  implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long userSenderId;

    private Long userReceiverId;

    private String content;

    private LocalDateTime createTime;


    public Chat(Long userSenderId, Long userReceiverId, String content) {
        this.userSenderId = userSenderId;
        this.userReceiverId = userReceiverId;
        this.content = content;

    }

    public static Chat insertChat(Long userSenderId, Long userReceiverId, String content) {
        Chat chat = new Chat(userSenderId, userReceiverId, content);
        chat.setCreateTime(LocalDateTime.now());
        return chat;
    }


    private static final long serialVersionUID = 1L;

    public String toString(){
        return "{\n" +
                "id:"+id+
                "\nuserSenderId:"+userSenderId+
                "\nuserReceiverId:"+userReceiverId+
                "\ncontent:"+content+
                "\ncreateTime:"+createTime+
                "\n}";
    }
}