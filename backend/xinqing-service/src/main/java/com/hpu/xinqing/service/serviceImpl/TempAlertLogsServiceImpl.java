package com.hpu.xinqing.service.serviceImpl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hpu.xinqing.mapper.TempAlertLogsMapper;
import com.hpu.xinqing.service.UserService;
import com.hpu.xinqingpojo.VO.TempAlertLogsVO;
import com.hpu.xinqingpojo.VO.UserVO;
import com.hpu.xinqingpojo.enmus.RoleEnums;
import com.hpu.xinqingpojo.entity.TempAlertLogs;
import com.hpu.xinqing.service.TempAlertLogsService;

import com.hpu.xinqingpojo.entity.User;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
* @description 针对表【temp_alert_logs】的数据库操作Service实现
* @createDate 2025-06-09 16:47:21
*/
@Service
public class TempAlertLogsServiceImpl extends ServiceImpl<TempAlertLogsMapper, TempAlertLogs>
    implements TempAlertLogsService{

    @Resource
    private UserService userService;

    @Override
    public IPage<TempAlertLogsVO> getAlertLogsByPage(int pageNum, int pageSize,long studentId) {
        if (pageNum < 1) {
            pageNum = 1;
        }
        if (pageSize <= 0) {
            pageSize = 10;
        }
        long userId = Long.parseLong(StpUtil.getLoginId().toString());
        LambdaQueryWrapper<User> studentQueryWrapper = new LambdaQueryWrapper<>();
        User teacher = userService.getById(userId);
        studentQueryWrapper.eq(User::getId,studentId);
        //查询对应的学生id
        if(teacher==null){
            throw new RuntimeException("用户不存在");
        }
        if(teacher.getRoleId()== RoleEnums.TEACHER.getType()){
           studentQueryWrapper.eq(User::getTeacherId,userId);
        }else if(teacher.getRoleId() == RoleEnums.STUDENT.getType() || teacher.getRoleId()== RoleEnums.VISITOR.getType()){
            throw new RuntimeException("用户权限不足");
        }
        List<User> studentList = userService.list(studentQueryWrapper);
        if(studentList.isEmpty()){
            throw new RuntimeException("该学生不是您的学生");
        }
        // 创建分页对象
        Page<TempAlertLogs> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<TempAlertLogs> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .eq(TempAlertLogs::getUserId,studentId)
                .orderByDesc(TempAlertLogs::getTriggerTime);
        page =this.page(page, queryWrapper);
        List<TempAlertLogsVO> records = new ArrayList<>();
        for (TempAlertLogs record : page.getRecords()) {
            TempAlertLogsVO tempAlertLogsVO = new TempAlertLogsVO();
            BeanUtil.copyProperties(record, tempAlertLogsVO);
            //获取学生
            UserVO userVOById = userService.getUserVOById(record.getUserId());
            if(userVOById != null){
                tempAlertLogsVO.setStudent(userVOById);
            }
            //获取老师
//            if(record.getHandlerId() !=null){
//                UserVO userTeacher = userService.getUserVOById(record.getHandlerId());
//                if(userTeacher != null){
//                    tempAlertLogsVO.setHandlerUser(userTeacher);
//                }
//            }
            records.add(tempAlertLogsVO);
        }
        Page<TempAlertLogsVO> pageResult = new Page<>(pageNum, pageSize);
        pageResult.setRecords(records);
        pageResult.setTotal(page.getTotal());
        return pageResult;
    }
}




