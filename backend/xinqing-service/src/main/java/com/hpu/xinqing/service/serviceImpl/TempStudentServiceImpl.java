package com.hpu.xinqing.service.serviceImpl;


import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hpu.xinqing.mapper.TempStudentMapper;
import com.hpu.xinqing.service.ITempStudentService;
import com.hpu.xinqing.service.UserService;
import com.hpu.xinqingpojo.VO.TempAlertLogsVO;
import com.hpu.xinqingpojo.VO.TempStudentVO;
import com.hpu.xinqingpojo.VO.UserVO;
import com.hpu.xinqingpojo.enmus.RoleEnums;
import com.hpu.xinqingpojo.entity.TempAlertLogs;
import com.hpu.xinqingpojo.entity.TempStudent;
import com.hpu.xinqingpojo.entity.User;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 心理危急学生表 服务实现类
 * </p>
 *
 * @since 2025-07-08
 */
@Service
public class TempStudentServiceImpl extends ServiceImpl<TempStudentMapper, TempStudent> implements ITempStudentService {

    @Resource
    private UserService userService;

    @Override
    public IPage<TempStudentVO> getStudentByPage(int pageNum, int pageSize) {
        if (pageNum < 1) {
            pageNum = 1;
        }
        if (pageSize <= 0) {
            pageSize = 10;
        }
        long userId = Long.parseLong(StpUtil.getLoginId().toString());
        LambdaQueryWrapper<User> studentQueryWrapper = new LambdaQueryWrapper<>();
        User teacher = userService.getById(userId);
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
            return new Page<>(pageNum, pageSize);
        }
        List<Long> collect = studentList.stream().map(User::getId).collect(Collectors.toList());
        // 创建分页对象
        Page<TempStudent> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<TempStudent> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .in(TempStudent::getStudentId,collect)
                .orderByAsc(TempStudent::getStatus)
                .orderByDesc(TempStudent::getAlertLevel)
                .orderByDesc(TempStudent::getCreateTime);
        page =this.page(page, queryWrapper);
        List<TempStudentVO> records = new ArrayList<>();
        for (TempStudent record : page.getRecords()) {
            TempStudentVO tempStudentVO = new TempStudentVO();
            BeanUtil.copyProperties(record, tempStudentVO);
            //获取学生
            UserVO userVOById = userService.getUserVOById(record.getStudentId());
            if(userVOById != null){
                tempStudentVO.setStudent(userVOById);
            }
            //获取老师
            if(record.getUpdater() !=null){
                UserVO userTeacher = userService.getUserVOById(record.getUpdater());
                if(userTeacher != null){
                    tempStudentVO.setHandlerUser(userTeacher);
                }
            }
            records.add(tempStudentVO);
        }
        Page<TempStudentVO> pageResult = new Page<>(pageNum, pageSize);
        pageResult.setRecords(records);
        pageResult.setTotal(page.getTotal());
        return pageResult;
    }

    /**
     * 处理用户的时候直接使这个学生所有记录都修改为已处理
     * @param studentId
     * @return
     */
    @Override
    public boolean updateByStudentId(Long studentId) {
        long teacherId = Long.parseLong(StpUtil.getLoginId().toString());
        User teacher = userService.getById(teacherId);
        if(teacher==null){
            throw new RuntimeException("用户不存在");
        }
        if(teacher.getRoleId() == RoleEnums.STUDENT.getType() || teacher.getRoleId()== RoleEnums.VISITOR.getType()){
            throw new RuntimeException("用户权限不足");
        }
        boolean update = lambdaUpdate().eq(TempStudent::getStudentId, studentId)
                .eq(TempStudent::getStatus, 0)
                .set(TempStudent::getStatus, 1)
                .set(TempStudent::getUpdater, teacherId)
                .set(TempStudent::getUpdateTime, LocalDateTime.now())
                .update();
        return update;
    }
}
