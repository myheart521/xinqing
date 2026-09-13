package com.hpu.xinqing.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.hpu.xinqingcommon.result.PageResult;
import com.hpu.xinqingpojo.DTO.PageDTO;
import com.hpu.xinqingpojo.VO.StudentAppointmentPageVo;
import com.hpu.xinqingpojo.VO.TeacherAppointmentPageVo;
import com.hpu.xinqingpojo.entity.AppTime;
import com.hpu.xinqingpojo.entity.Appointment;
import com.hpu.xinqingpojo.entity.AppointmentDTO;

import java.time.LocalDate;
import java.util.List;

/**
 * <p>
 * 预约 服务类
 * </p>
 *
 * @since 2025-07-12
 */
public interface IAppointmentService extends IService<Appointment> {


    PageResult<TeacherAppointmentPageVo> teacherPageQuery(PageDTO pageDTO, String name);

    PageResult<StudentAppointmentPageVo> studentPageQuery(PageDTO pageDTO);

    List<AppTime> getStartTime(Long teacherId, LocalDate date);

    List<AppTime> getEndTime(Long teacherId, Integer startTime, LocalDate date);

    void create(AppointmentDTO appointment);

    void updateById(AppointmentDTO appointment);

}
