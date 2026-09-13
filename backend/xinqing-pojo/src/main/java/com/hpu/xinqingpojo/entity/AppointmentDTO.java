package com.hpu.xinqingpojo.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * <p>
 * 预约
 * </p>
 *
 * @since 2025-07-12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class AppointmentDTO implements Serializable {

    private Long id;

    /**
     * 老师id,使用user表user_id
     */
    private Long teacherId;

    /**
     * 日期
     */
    @JSONField(format = "yyyy-MM-dd")
    private LocalDate date;

    /**
     * 开始时间
     */
    private Integer startTime;

    /**
     * 结束时间
     */
    private Integer endTime;

    /**
     * 理由
     */
    private String excuse;

    //状态
    private Integer status;


}
