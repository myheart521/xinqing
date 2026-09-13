package com.hpu.xinqingpojo.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @since 2024-11-04
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("smartwatch")
public class Smartwatch implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 睡眠时间，单位小时
     */
    private Float sleepTime;

    private LocalDateTime createTime;

    /**
     * 久坐的时间
     */
    private Float sedentaryTime;

    private String userId;

    private LocalDateTime deviceTime;

    private Float heartRate;

    private Float bodyTemperature;

    private String equipId;


}
