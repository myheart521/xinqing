package com.hpu.xinqing.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.hpu.xinqingpojo.entity.Student;

/**
 * <p>
 * 学生表 服务类
 * </p>
 *
 * @since 2025-05-29
 */
public interface IStudentService extends IService<Student> {

    Student updateScoreByUserId(Long userId, int score);
}
