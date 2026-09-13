package com.hpu.xinqingpojo.VO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentAppointmentPageVo {
    /**
     * 学号
     */
    private String studentNumber;
    /**
     * 学生 昵称
     */
    private String studentName;
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
     * 教师 昵称
     */
    private String teacherName;
    /**
     * 状态：0：未处理，1：通过，2：驳回，3：取消
     */
    private String status;

    private String excuse;


}
