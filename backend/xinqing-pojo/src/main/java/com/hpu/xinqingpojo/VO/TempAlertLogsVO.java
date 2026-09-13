package com.hpu.xinqingpojo.VO;

import com.hpu.xinqingpojo.entity.User;
import dev.langchain4j.model.output.structured.Description;
import lombok.Data;

import java.util.Date;

@Data
public class TempAlertLogsVO {
    @Description("关联的情感记录")
    private TempEmotionRecordsVO emotionRecordsVO;

    @Description("学生信息")
    private UserVO student;

    @Description("触发报警的时间")
    private Date triggerTime;

    @Description("报警等级 0-低级、1-中级、2紧急")
    private Integer alertLevel;

    @Description("处理状态 0-未处理、1-已处理")
    private Integer handlingStatus;

    @Description("处理人信息")
    private UserVO handlerUser;

    @Description("处理方式（0-电话、1-邮箱、2-线下、3-其他）")
    private Integer handleWay;

    @Description("是否解决 0-未解决、1-已解决")
    private Integer isResolve;

    @Description("备注")
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
