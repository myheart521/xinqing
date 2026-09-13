package com.hpu.xinqingpojo.VO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TeacherAppointmentPageVo {


    private Long id;
    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 用户头像
     */
    private String userAvatar;
    /**
     * 用户头像
     */
    private String studentNumber;
    /**
     * 用户手机号
     */
    private String phone;
    //性别
    private String sex;
    //学院
    private String college;
    //专业班级，eg：软件2203
    private String majorClass;
    //以上是学生表

    private String date;
    /**
     * 开始时间
     */
    private String startTime;

    /**
     * 结束时间
     */
    private String endTime;

    /**
     * 状态：0：未处理，1：通过，2：驳回，3：取消
     */
    private String status;
    private String excuse;
}
