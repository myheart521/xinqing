package com.hpu.xinqing.service.serviceImpl;


import com.hpu.xinqing.mapper.StudentMapper;
import com.hpu.xinqing.service.IStudentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hpu.xinqingpojo.entity.Student;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 学生表 服务实现类
 * </p>
 *
 * @since 2025-05-29
 */
@Service
public class StudentServiceImpl extends ServiceImpl<StudentMapper, Student> implements IStudentService {

    @Override
    public Student updateScoreByUserId(Long userId, int score) {
        boolean update = update().eq("user_id", userId)
                .setSql("score=score+" + score)
                .update();
        if (update) {
            return lambdaQuery().eq(Student::getUserId, userId).one();
        }
        return null;
    }
}
