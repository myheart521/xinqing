package com.hpu.xinqingpojo.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @since 2024-09-11
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("mbti_topic")
public class MbtiTopic implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 题目
     */
    private String name;

    /**
     * A选项
     */
    private String optionA;

    /**
     * B选项
     */
    private String optionB;

    /**
     * 序号
     */
    private Integer sequenceNumber;

    /**
     * A选项对应值
     */
    private String valA;

    /**
     * B选项对应值
     */
    private String valB;


}
