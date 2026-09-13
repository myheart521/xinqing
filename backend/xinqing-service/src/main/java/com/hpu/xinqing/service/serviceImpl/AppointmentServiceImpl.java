package com.hpu.xinqing.service.serviceImpl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hpu.xinqing.mapper.AppTimeMapper;
import com.hpu.xinqing.mapper.AppointmentMapper;
import com.hpu.xinqing.service.IAppointmentService;
import com.hpu.xinqingcommon.exception.BaseException;
import com.hpu.xinqingcommon.result.PageResult;
import com.hpu.xinqingcommon.utils.StpUtils;
import com.hpu.xinqingpojo.DTO.PageDTO;
import com.hpu.xinqingpojo.VO.StudentAppointmentPageVo;
import com.hpu.xinqingpojo.VO.TeacherAppointmentPageVo;
import com.hpu.xinqingpojo.entity.AppTime;
import com.hpu.xinqingpojo.entity.Appointment;
import com.hpu.xinqingpojo.entity.AppointmentDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AppointmentServiceImpl extends ServiceImpl<AppointmentMapper, Appointment> implements IAppointmentService {

    @Autowired
    private AppointmentMapper appointmentMapper;
    @Autowired
    private AppTimeMapper timeDOMapper;





    @Override
    public List<AppTime> getStartTime(Long teacherId, LocalDate date) {
        LocalDate now = LocalDate.now();
        boolean before = now.plusDays(30).isBefore(date);
        if(date.isBefore(now)){
            throw new BaseException("预约日期错误");
        }
        boolean isNow = now.equals(date);
        if (before) {
            throw new BaseException("预约时间不能超过30天");
        }
        QueryWrapper<Appointment> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(Appointment::getTeacherId, teacherId)
                .eq(Appointment::getDate, date)
                .in(Appointment::getStatus, Arrays.asList("1", "0"));
        List<Appointment> reservationDOS = baseMapper.selectList(queryWrapper);
        //lt < ,gt >,ge >=,le <=
        LambdaQueryWrapper<AppTime> lambda = new LambdaQueryWrapper<>();
        if (isNow) {
            lambda.gt(AppTime::getTime, LocalTime.now());
        }
        lambda.le(AppTime::getTime, LocalTime.of(18, 0));
        for (Appointment reservationDO : reservationDOS) {
            LocalTime startTime = reservationDO.getStartTime();
            LocalTime endTime = reservationDO.getEndTime();
            lambda.and(wrapper -> wrapper.lt(AppTime::getTime, startTime).or().ge(AppTime::getTime, endTime));
        }
        List<AppTime> timeDOS = timeDOMapper.selectList(lambda);
        if (timeDOS == null && timeDOS.size() == 0) {
            //说明没有开始时间
            throw new BaseException("不存在有效的开始时间");
        }
        List<AppTime> list = timeDOS.stream().map(timeDO -> {
            AppTime timeVO = new AppTime();
            timeVO.setId(timeDO.getId());
            timeVO.setValue(timeDO.getTime().toString());
            return timeVO;
        }).collect(Collectors.toList());
        return list;
    }

    @Override
    public List<AppTime> getEndTime(Long teacherId, Integer startTime, LocalDate date) {
        //获取开始时间
        LocalTime start = getTime(startTime);
        QueryWrapper<Appointment> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(Appointment::getTeacherId, teacherId)
                .eq(Appointment::getDate, date)
                //预约状态。0:未处理。1：预约成功。2：预约失败。3：取消预约
                .in(Appointment::getStatus, Arrays.asList("0", "1"))
                //大于开始时间
                .gt(Appointment::getStartTime, start)
                .orderByAsc(Appointment::getStartTime);
        List<Appointment> reservationDOS = baseMapper.selectList(queryWrapper);
        QueryWrapper<AppTime> timeQuery = new QueryWrapper<>();
        //lt < ,gt >,ge >=,le <=

        LambdaQueryWrapper<AppTime> lambda = timeQuery.lambda();
        lambda.gt(AppTime::getTime, start);
        if (reservationDOS != null && reservationDOS.size() > 0) {
            LocalTime startTime1 = reservationDOS.get(0).getStartTime();
            lambda.le(AppTime::getTime, startTime1);
        }
        if (start.isBefore(LocalTime.of(12, 0))) {
            lambda.le(AppTime::getTime, LocalTime.of(12, 0));
        }
        List<AppTime> timeDOS = timeDOMapper.selectList(timeQuery);
        if (timeDOS == null && timeDOS.size() == 0) {
            //说明没有可选的结束时间
            throw new BaseException("不存在有效的结束时间");
        }
        List<AppTime> list = timeDOS.stream().map(timeDO -> {
            AppTime timeVO = new AppTime();
            timeVO.setId(timeDO.getId());
            timeVO.setValue(timeDO.getTime().toString());
            return timeVO;
        }).collect(Collectors.toList());
        return list;
    }

    @Override
    public void create(AppointmentDTO appointment) {
        Long userId = StpUtils.userId();
        LocalTime start=getTime(appointment.getStartTime());
        LocalTime end=getTime(appointment.getEndTime());
        Appointment appointment1 = BeanUtil.copyProperties(appointment, Appointment.class);
        appointment1.setStudentId(userId);
        appointment1.setStartTime(start);
        appointment1.setEndTime(end);
        appointment1.setStatus(0);
        boolean save = save(appointment1);
        if(!save){
            throw new BaseException("预约失败");
        }

    }

    @Override
    public void updateById(AppointmentDTO appointment) {
        Long id = appointment.getId();
        Appointment byId = getById(id);
        Integer status = appointment.getStatus();
        if(byId==null){
            throw new BaseException("预约信息不存在");
        }
        if(byId.getStatus()!=0 && status !=3){
            throw new BaseException("预约信息已处理");
        }
        byId.setStatus(status);
        if(ObjectUtils.isNotEmpty(appointment.getExcuse())){
            byId.setExcuse(appointment.getExcuse());
        }
        byId.setUpdateTime(LocalDateTime.now());
        lambdaUpdate().eq(Appointment::getId,id)
                .update(byId);
    }

    public LocalTime getTime(Integer time) {
        if (time == null) {
            throw new BaseException("开始时间不能为空");
        }
        int hour = time / 60;
        int minute = time % 60;
        if(hour<8 || hour>19){
            throw new BaseException("预约时间不在8:00-18:00之间");
        }
        return LocalTime.of(hour, minute);
    }

    @Override
    public PageResult<TeacherAppointmentPageVo> teacherPageQuery(PageDTO pageDTO, String name) {
        // 1. 获取当前教师ID
        Long teacherId = StpUtils.userId();
//        Long teacherId =24l;
        // 2. 创建 MyBatis-Plus 分页对象
        Page<TeacherAppointmentPageVo> page = new Page<>(pageDTO.getPageNo(), pageDTO.getPageSize());

        // 3. 执行分页查询
        Page<TeacherAppointmentPageVo> teacherAppointmentPageVoPage = appointmentMapper.selectTeacherAppointments(page, teacherId, name);
        PageResult<TeacherAppointmentPageVo> teacherAppointmentPageVoPageResult = new PageResult<>(teacherAppointmentPageVoPage.getTotal(), teacherAppointmentPageVoPage.getRecords());

        return teacherAppointmentPageVoPageResult;
    }

    @Override
    public PageResult<StudentAppointmentPageVo> studentPageQuery(PageDTO pageDTO) {
        // 1. 获取当前登录学生ID
        Long studentId = StpUtils.userId();
//        Long studentId = 14l;
        // 2. 创建 MyBatis-Plus 分页对象
        Page<StudentAppointmentPageVo> page = new Page<>(pageDTO.getPageNo(), pageDTO.getPageSize());

        // 3. 执行分页查询
        Page<StudentAppointmentPageVo> resultPage = appointmentMapper.selectStudentAppointments(page, studentId);

        // 4. 转换为自定义分页结果
        PageResult<StudentAppointmentPageVo> studentAppointmentPageVoPageResult = new PageResult<>(resultPage.getTotal(), resultPage.getRecords());
        return studentAppointmentPageVoPageResult;
    }
}
