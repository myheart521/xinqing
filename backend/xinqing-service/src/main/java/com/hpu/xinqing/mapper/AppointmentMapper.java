package com.hpu.xinqing.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hpu.xinqingpojo.VO.StudentAppointmentPageVo;
import com.hpu.xinqingpojo.VO.TeacherAppointmentPageVo;
import com.hpu.xinqingpojo.entity.Appointment;
import org.apache.ibatis.annotations.Param;

public interface AppointmentMapper extends BaseMapper<Appointment> {
    Page<TeacherAppointmentPageVo> selectTeacherAppointments(
            @Param("page") Page<TeacherAppointmentPageVo> page,
            @Param("teacherId") Long teacherId,
            @Param("name") String name);

    Page<StudentAppointmentPageVo> selectStudentAppointments(
            @Param("page") Page<StudentAppointmentPageVo> page,
            @Param("studentId") Long studentId);
}
