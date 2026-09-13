package com.hpu.xinqing.controller;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.hpu.xinqing.service.IAppointmentService;
import com.hpu.xinqingcommon.result.PageResult;
import com.hpu.xinqingcommon.result.Result;
import com.hpu.xinqingpojo.DTO.PageDTO;
import com.hpu.xinqingpojo.VO.StudentAppointmentPageVo;
import com.hpu.xinqingpojo.VO.TeacherAppointmentPageVo;
import com.hpu.xinqingpojo.entity.AppTime;
import com.hpu.xinqingpojo.entity.AppointmentDTO;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("appointment")
public class AppointmentController {
    @Autowired
    private IAppointmentService appointmentService;


    @PostMapping("/create")
    public Result create(@RequestBody AppointmentDTO appointment) {
        appointmentService.create(appointment);
        return Result.success();
    }

    @PutMapping("/update")
    public Result update(@RequestBody AppointmentDTO appointment) {
        appointmentService.updateById(appointment);
        return Result.success();
    }


    @GetMapping("/startTime")
    public Result<List<AppTime>> getStartTime(
            @RequestParam("teacherId") Long teacherId,
            @RequestParam(value = "date")
            @JsonFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        List<AppTime> list = appointmentService.getStartTime(teacherId, date);
        return Result.success(list);
    }

    @GetMapping("/endTime")
    @Operation(summary = "获得座位结束时间")
    public Result<List<AppTime>> getEndTime(@RequestParam("teacherId") Long teacherId
            , @RequestParam(value = "startTime") Integer startTime
            , @RequestParam(value = "date", required = false)
             @JsonFormat(pattern = "yyyy-MM-dd") LocalDate date
    ) {
        List<AppTime> list = appointmentService.getEndTime(teacherId, startTime, date);
        return Result.success(list);
    }

    @GetMapping("teacher")
    public Result teacherGet(PageDTO pageDTO, String name) {

        PageResult<TeacherAppointmentPageVo> teacherAppointmentPageVo = appointmentService.teacherPageQuery(pageDTO, name);
        return Result.success(teacherAppointmentPageVo);
    }

    @GetMapping("student")
    public Result teacherStudent(PageDTO pageDTO) {

        PageResult<StudentAppointmentPageVo> studentAppointmentPageVo = appointmentService.studentPageQuery(pageDTO);
        return Result.success(studentAppointmentPageVo);
    }


}
