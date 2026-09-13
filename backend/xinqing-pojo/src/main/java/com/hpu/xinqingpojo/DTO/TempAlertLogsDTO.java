package com.hpu.xinqingpojo.DTO;

import com.baomidou.mybatisplus.annotation.TableName;
import dev.langchain4j.model.output.structured.Description;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * @TableName temp_alert_logs
 */
@TableName(value ="temp_alert_logs")
@Data
@Description("报警记录表")
public class TempAlertLogsDTO {
    @Description("关联的情感记录表id")
    private Long emotionRecordId;

    @Description("学生id")
    private Integer userId;

    @Description("报警等级 0-低级、1-中级、2紧急")
    private Integer alertLevel;

    @Description("最近的行为特征")
    private String recentBehavior;

    @Description("建议的处理方式")
    private String suggestion;
}
