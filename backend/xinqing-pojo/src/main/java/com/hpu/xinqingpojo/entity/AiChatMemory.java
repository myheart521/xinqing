package com.hpu.xinqingpojo.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 聊天回话表
 * </p>
 *
 * @since 2025-03-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("ai_chat_memory")
public class AiChatMemory implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * UUID对应的回话id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private String id;

    private Long userId;

    /**
     * 回话名
     */
    private String name;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;


}
