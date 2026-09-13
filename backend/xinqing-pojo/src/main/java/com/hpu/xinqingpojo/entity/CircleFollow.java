package com.hpu.xinqingpojo.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 用户之间关注信息
 * </p>
 *
 * @since 2025-02-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("circle_follow")
public class CircleFollow implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    /**
     * 关注人id
     */
    private Long userId;

    /**
     * 被关注的用户id
     */
    private Long circleId;

    /**
     * 关注时间
     */
    private LocalDateTime createTime;


}
