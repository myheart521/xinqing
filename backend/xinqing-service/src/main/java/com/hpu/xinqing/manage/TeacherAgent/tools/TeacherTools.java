package com.hpu.xinqing.manage.TeacherAgent.tools;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.dashscope.app.Application;
import com.alibaba.dashscope.app.ApplicationParam;
import com.alibaba.dashscope.app.ApplicationResult;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.hpu.xinqing.manage.TeacherAgent.template.StudentActivityQuery;
import com.hpu.xinqing.manage.TeacherAgent.template.StudentBlogQuery;
import com.hpu.xinqing.manage.TeacherAgent.template.StudentCommentQuery;
import com.hpu.xinqing.service.IActivityService;
import com.hpu.xinqing.service.UserService;
import com.hpu.xinqingcommon.constant.RedisConstant;
import com.hpu.xinqingpojo.entity.User;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import jakarta.annotation.Resource;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.stringtemplate.v4.ST;

import java.time.Duration;


//@Setter
@Slf4j
@Component
public class TeacherTools {
    @Resource
    private IActivityService activityService;
    @Resource
    private UserService userService;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private StudentActivityQuery studentActivityQuery;
    @Resource
    private StudentBlogQuery studentBlogQuery;
    @Resource
    private StudentCommentQuery studentCommentQuery;

    //抽象模板类注入

    //配置redis过期时间为24h
    private static final long EXPIRE_TIME = 24 * 60 * 60;

    /**
     * 获取学生信息
     *
     * @param studentNumber 学号
     * @return 学生信息
     */
    @Tool("根据输入的学号获取学生发布的详细信息")
    public String getStudentInfo(@P("学生的学号")String studentNumber) throws JsonProcessingException {
        User student = new User();
        //如果传递了学号那么根据学号查找学生信息
        if (ObjectUtil.isNotEmpty(studentNumber)) {
            User userByRedis = this.getUserByRedis(studentNumber);
            if (ObjectUtil.isEmpty(userByRedis)) {
                return "未找到该学生信息";
            } else {
                return "该学生信息为：" + this.formatStudentInfo(userByRedis);
            }
        } else {
            return "请传递学生学号信息";
        }
    }

    /**
     * 获取学生参与活动的详细信息
     *
     * @param studentNumber 学号
     * @param activeNumber  查询活动条数
     * @return 学生参与活动的详细信息
     */
    @Tool("根据学号获取学生参与活动的详细信息，可以指明查询几条，默认最近2个")
    public String getStudentActivityInfo (String studentNumber, Integer activeNumber) throws JsonProcessingException {
        //此处使用模板方法策略模式实现封装不需要重复写重复的代码
        return studentActivityQuery.executeQuery(studentNumber,activeNumber);
    }
    @Tool("根据学号获取学生发布的博客的详细信息，可以指明查询几条，默认最近2条")
    public String getStudentDynamicInfo(String studentNumber, Integer blogNumber) throws JsonProcessingException {
        //此处使用模板方法策略模式实现封装不需要重复写重复的代码
        return studentBlogQuery.executeQuery(studentNumber,blogNumber);
    }

    @Tool("""
            该工具是一个地图的智能体，具有以下功能，提供全场景覆盖的地理信息服务，
            包括地理编码、逆地理编码、IP定位、天气查询、骑行路径规划、步行路径规划、驾车路径规划、
            公交路径规划、距离测量、关键词搜索、周边搜索、详情搜索等。当需要上述功能时直接传入问题""")
    public String getGaoDeMCP(@P("需求描述")String description){
        String response=null;
        try {
            response=getBaiLian(description);
        } catch (Exception e) {
            log.error("调用百炼MCP异常："+e);
        }
        if (StrUtil.isEmpty(response)){
            return "抱歉，我没有找到相关信息";
        }
        return response;
    }


    @Tool(value = "根据学号获取学生发布的评论，可以指明查询几个，默认是5个")
    public String getStudentCommentInfo(String studentNumber, Integer commentNumber) throws JsonProcessingException {
        //此处使用模板方法策略模式实现封装不需要重复写重复的代码
        return studentCommentQuery.executeQuery(studentNumber,commentNumber);
    }

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

    /**
     * 格式化学生信息
     *
     * @param student 学生信息
     * @return 学生信息字符串
     */
    private String formatStudentInfo(User student) {
        return "学号：" + student.getStudentNumber()
                + "，姓名：" + student.getUserName()
                + "，学院：" + student.getCollege()
                + "，专业班级：" + student.getMajorClass()
                + "，性别：" + student.getSex()
                + "，电话：" + student.getPhone()
                + "，邮箱：" + student.getEmail();
    }

    public String getBaiLian(String question)
            throws ApiException, NoApiKeyException, InputRequiredException {
        ApplicationParam param = ApplicationParam.builder()
                // 若没有配置环境变量，可用百炼API Key将下行替换为：.apiKey(System.getenv("DASHSCOPE_API_KEY"))。但不建议在生产环境中直接将API Key硬编码到代码中，以减少API Key泄露风险。
                .apiKey(System.getenv("DASHSCOPE_API_KEY"))
                .appId(System.getenv("DASHSCOPE_APP_ID"))
                .prompt(question)
                .build();

        Application application = new Application();
        ApplicationResult result = application.call(param);

        return result.getOutput().getText();
    }




}
