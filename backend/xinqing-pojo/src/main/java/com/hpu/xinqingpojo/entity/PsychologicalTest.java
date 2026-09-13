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
 * 心理测试
 * </p>
 *
 * @since 2024-09-11
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("psychological_test")
public class PsychologicalTest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 问题
     */
    private String question;

    private String optionsA;

    private String optionsB;

    private String optionsC;

    private String optionsD;

    private String optionsE;

    private Integer valA;

    private Integer valB;

    private Integer valC;

    private Integer valD;

    private Integer valE;


}
