package com.hpu.xinqing.manage.TeacherAgent.template;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.hpu.xinqing.service.UserService;
import com.hpu.xinqingcommon.constant.RedisConstant;
import com.hpu.xinqingpojo.entity.Activity;
import com.hpu.xinqingpojo.entity.User;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;

/**
 * 使用模板策略来封装获取学生信息的各个操作简化不重复
 */

public abstract class AbstractStudentQueryTemplate {
    //    @Resource
//    protected  StringRedisTemplate redisTemplate;
    @Resource
    private StringRedisTemplate stringRedisTemplate; // 使用泛型字符串类型

    @Resource
    protected UserService userService;

    //配置redis过期时间为24h
    private static final long EXPIRE_TIME = 24 * 60 * 60;

    /**
     * 获取学生信息
     *
     * @param studentNumber 学号
     * @param count         数量
     * @return 学生信息字符串化的吧
     * @throws JsonProcessingException
     */
    public final String executeQuery(String studentNumber, Integer count) throws JsonProcessingException {
        if (ObjectUtil.isEmpty(studentNumber)) {
            count = 2;
        }
        if (ObjectUtil.isEmpty(studentNumber)) {
            return "请传递学生学号信息";
        }
        //1。从redis及数据库查user这是一个通用的
        User user = this.getUserByRedis(studentNumber);
        if (ObjectUtil.isEmpty(user)) {
            return "未找到该学生信息";
        }
        //2。获取数据列表，此处不是通用的需要抽象出来，外部实现
        List<?> dataList = fetchData(user.getId(), count);
        if (ObjectUtil.isEmpty(dataList)) {
            return "未找到相关记录";
        }
        //3。格式化数据，此处不是通用的需要抽象出来，外部实现
        String formattedResult = formatData(dataList);
        //4。拼接结果也是同用的
        return String.format("共找到 %d 条记录：%n%s", dataList.size(), formattedResult);
    }

    /**
     * 差异化方法 1：查询具体数据
     */
    protected abstract List<?> fetchData(Long userId, Integer count);

    /**
     * 差异化方法 2：格式化数据
     */
    protected abstract String formatData(List<?> dataList);

    /**
     * 从redis中获取学生信息
     *
     * @param studentNumber 学号
     * @return 学生对象
     */
    private User getUserByRedis(String studentNumber) throws JsonProcessingException {
        String keyPre = RedisConstant.TEACHER_STUDENT_INFO + studentNumber;
        String jsonUser = stringRedisTemplate.opsForValue().get(keyPre);
        User student = null;
        if (ObjectUtil.isNotEmpty(jsonUser)) {
            student = JSONUtil.toBean(jsonUser, User.class);  // JSON 转为对象
        }
        if (ObjectUtil.isNotEmpty(student)) {
            return student;
        }
        User studentFromDB = userService.getUserByStudentId(studentNumber);
        if (ObjectUtil.isNotEmpty(studentFromDB)) {
            saveUserToRedis(studentFromDB);
            return studentFromDB;
        } else {
            return null;
        }
    }


    /**
     * 将值保存到redis中
     *
     * @param user 用户对象
     */
    private void saveUserToRedis(User user) throws JsonProcessingException {
        String key = RedisConstant.TEACHER_STUDENT_INFO + user.getStudentNumber();
        // 使用 Jackson 转换为 JSON 字符串
        String jsonUser = JSONUtil.toJsonStr(user);
        stringRedisTemplate.opsForValue().set(key, jsonUser, Duration.ofDays(EXPIRE_TIME));
    }


}
