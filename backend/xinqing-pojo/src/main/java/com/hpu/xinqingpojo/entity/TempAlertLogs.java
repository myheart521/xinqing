package com.hpu.xinqingpojo.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;

import dev.langchain4j.model.output.structured.Description;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @since 2025-07-06
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("temp_alert_logs")
public class TempAlertLogs implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 关联情绪记录ID
     */
    private Long emotionRecordId;

    private Long userId;

    /**
     * 触发时间
     */
    private LocalDateTime triggerTime;

    /**
     * 预警级别0-低级、1-中级、2-紧急
     */
    private Integer alertLevel;

    /**
     * 处理状态0-未处理、1-已处理
     */
    private Integer handlingStatus;

    /**
     * 处理人员ID（关联老师）
     */
    private Long handlerId;

    /**
     * 处理方式（0-电话、1-邮箱、2-线下、3-其他）
     */
    private Integer handleWay;

    /**
     * 是否解决
     */
    private Boolean isResolve;

    /**
     * 备注
     */
    private String note;

    /**
     * 最近的行为特征
     */
    private String recentBehavior;

    /**
     * 建议的处理方式
     */
    private String suggestion;


}
