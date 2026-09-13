package com.hpu.xinqing.manage.TeacherAgent.tools;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.hpu.xinqing.manage.ClaudeManager.model.MentalAnalysisResult;
import com.hpu.xinqing.manage.ClaudeManager.service.impl.MentalAnalysisServiceImpl;
import com.hpu.xinqing.manage.TeacherAgent.template.StudentActivityQuery;
import com.hpu.xinqing.manage.TeacherAgent.template.StudentBlogQuery;
import com.hpu.xinqing.manage.TeacherAgent.template.StudentCommentQuery;
import com.hpu.xinqing.service.*;
import com.hpu.xinqing.service.ai.AssistantTeacherMCP;
import com.hpu.xinqing.utils.sse.ExcelService;
import com.hpu.xinqing.utils.sse.FileTransferService;
import com.hpu.xinqing.utils.sse.SseManager;
import com.hpu.xinqing.utils.sse.enums.FileType;
import com.hpu.xinqingcommon.constant.RedisConstant;
//import com.hpu.xinqingcommon.utils.JsonSafeUtil;
import com.hpu.xinqingcommon.utils.JsonSafeUtil;
import com.hpu.xinqingpojo.DTO.NewKnowledgeDTO;
import com.hpu.xinqingpojo.entity.HistoryTest;
import com.hpu.xinqingpojo.entity.Modules;
import com.hpu.xinqingpojo.entity.User;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.internal.Utils;
import dev.langchain4j.web.search.WebSearchEngine;
import dev.langchain4j.web.search.WebSearchResults;
import jakarta.annotation.Resource;
import org.apache.poi.ss.usermodel.*;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;


//@Setter
@Component
public class TeacherBasicTools {
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

    @Resource
    private MentalAnalysisServiceImpl mentalAnalysisService;

    @Resource
    @Lazy
    private AssistantTeacherMCP assistantTeacherMCP;

    //发布文章
    @Resource
    private TeacherService teacherService;

    @Resource
    private HistoryTestService historyTestService;

    @Resource
    private ModulesService modulesService;


    @Resource
    private WebSearchEngine searchEngine;


    @Resource
    private SseManager sseManager;

    //发送文件
    @Resource
    private FileTransferService fileTransferService;


    //发送excel文件
    @Resource
    private ExcelService excelService;

    //配置redis过期时间为24h
    private static final long EXPIRE_TIME = 24 * 60 * 60;


    /**
     * 获取学生信息
     *
     * @param studentNumber 学号
     * @return 学生信息
     */
    @Tool("根据输入的学号获取学生发布的详细信息")
    public String getStudentInfo(@P("学生学号") String studentNumber) throws JsonProcessingException {
        User student = new User();
        //如果传递了学号那么根据学号查找学生信息
        if (ObjectUtil.isNotEmpty(studentNumber)) {
            User userByRedis = this.getUserByRedis(studentNumber);
            if (ObjectUtil.isEmpty(userByRedis)) {
                return "未找到该学生信息";
            } else {
                if (userByRedis != null) {
                    return "该学生信息为：" + this.formatStudentInfo(userByRedis);
                } else {
                    return "未找到该学生信息";
                }
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
    public String getStudentActivityInfo(@P("学生学号") String studentNumber, @P("查询活动的条数，默认最近2条") Integer activeNumber) throws JsonProcessingException {
        //此处使用模板方法策略模式实现封装不需要重复写重复的代码
        return studentActivityQuery.executeQuery(studentNumber, activeNumber);
    }
    /**
     * 获取学生发布博客的详细信息
     *
     * @param studentNumber 学号
     * @param blogNumber    查询博客条数
     * @return 博客信息
     */
    @Tool("根据学号获取学生发布的博客动态的详细信息，可以指明查询几条，默认最近2条")
    public String getStudentDynamicInfo(@P("学生学号") String studentNumber, @P("查询博客动态的条数，默认最近2条") Integer blogNumber) throws JsonProcessingException {
        //此处使用模板方法策略模式实现封装不需要重复写重复的代码
        return studentBlogQuery.executeQuery(studentNumber, blogNumber);
    }
    /**
     * 获取学生发布的评论的详细信息
     *
     * @param studentNumber 学号
     * @param commentNumber 查询评论条数
     * @return 学生发布的评论的详细信息
     */
    @Tool("根据学号获取学生发布的评论，可以指明查询几个，默认是5个")
    public String getStudentCommentInfo(@P("学生学号") String studentNumber, @P("评论条数，默认为5条") Integer commentNumber) throws JsonProcessingException {
        //此处使用模板方法策略模式实现封装不需要重复写重复的代码
        return studentCommentQuery.executeQuery(studentNumber, commentNumber);
    }
    @Tool("根据学号获取学生心理状态")
    public String getStudentMood(@P("学生学号") String studentNumber) throws IOException {
        if (ObjectUtil.isEmpty(studentNumber)) {
            return "请传递学生学号信息";
        }
        User userByRedis = getUserByRedis(studentNumber);
        //调用服务层获取学生心理状态
        MentalAnalysisResult mentalAnalysisResult = mentalAnalysisService.analyzeUserBehavior(userByRedis.getId());
        return formatStudentMentalAnalysis(mentalAnalysisResult);
    }
    /**
     * 文件保存本地，可选择保存的格式，使用SSE传输给前端
     *
     * @param data     需要保存的数据
     * @param format   保存的格式
     * @param filename 文件名，默认为null
     * @param userId   登录用户的id
     * @return 保存结果
     */
    @Tool("生成文件的工具，可以选择文件的格式")
    public String saveFileToLocal(@P("需要保存到文件中的数据") String data, @P("文件保存的格式，默认格式为TXT") FileType format, @P("文件名，默认为null") String filename, @P("登录用户的id") Long userId) throws Exception {
        if (ObjectUtil.isEmpty(format)) {
            format = FileType.TXT;
        }

        // 处理可能包含换行符或特殊字符的数据
        String processedData = JsonSafeUtil.escapeForDataTransfer(data);

        //先检查连接状态
        if (!sseManager.hasActiveConnection(userId)) {
            return "当前用户没有连接，请先连接";
        }
        // 启动文件传输
        boolean started = fileTransferService.transferFile(userId, processedData, format, filename);
        if (!started) {
            return "文件传输服务未启动，请检查";
        }
        return "文件传输服务已启动，请等待传输完成";
    }
    /**
     * @param content 发布的内容
     */
    @Tool("操作浏览器")
    public String operateBrowser(@P("需要操作的内容") String content) throws IOException {
        // 处理可能包含换行符的输入
        String processedContent = JsonSafeUtil.toSingleLine(content);
        return assistantTeacherMCP.chat("456", "操作浏览器" + processedContent);
    }
    /**
     * @param title   文章标题
     * @param desc    文章描述
     * @param tag     文章标签
     * @param content 文章内容
     * @return 发布的内容
     */
    @Tool("发布文章")
    public String publishArticle(@P("标题") String title, @P("描述（简短）") String desc, @P("标签") String tag, @P("使用HTML富文本内容") String content) throws IOException {
        // 处理可能包含特殊字符的内容
        String processedContent = JsonSafeUtil.escapeHtmlForJson(content);

        NewKnowledgeDTO knowledgeDTO = new NewKnowledgeDTO();
        knowledgeDTO.setTitle(title);
        knowledgeDTO.setDescriptions(desc);
        knowledgeDTO.setTags(tag);
        knowledgeDTO.setContent(processedContent);
        knowledgeDTO.setColor("red");
        knowledgeDTO.setCoverImage("https://p8.itc.cn/images01/20210317/b82a2d532d544da1a19bcf9cc3d42cf1.jpeg");
        teacherService.publish(knowledgeDTO);
        return "发布文章成功，发布文章内容为：" + knowledgeDTO.getTitle();
    }
    /**
     * 根据学号导出学生的测评结果记录
     *
     * @param userId        登录用户id
     * @param studentNumber 学生学号
     * @param count         查询几条数据，如果没穿默认为10条
     * @return 导出结果
     */
    @Tool("根据学号导出学生的测评结果记录")
    public String exportStudentTestResult(@P("登录用户id") Long userId, @P("学生学号") String studentNumber, @P("查询几条数据，如果没传默认为10条") Integer count) throws IOException {
        if (ObjectUtil.isEmpty(studentNumber)) {
            return "请传递学生学号信息";
        }
        User user = getUserByRedis(studentNumber);
        if (ObjectUtil.isEmpty(user)) {
            return "未查询到该学生信息，请检查学号是否正确";
        }
        List<HistoryTest> historyByUserId = historyTestService.getHistoryByUserId(user.getId(), count);
        //格式化为excel
        List<Map<String, Object>> data = textHistoryFormate(historyByUserId);
        List<String> headers = List.of("测评时间", "测评结果", "测评名称", "测评介绍", "测评描述", "测评介绍");
        //userId为接收文件者
        boolean success = excelService.transferExcelFile(userId, "学生测评记录", "测评记录", headers, data, null);
        if (success) {
            return "导出学生测评记录成功";
        } else {
            return "导出学生测评记录失败";
        }
    }

    /**
     * 导出学生列表
     *
     * @param userId 登录用户id
     * @param count  查询几条数据，如果没传默认为10条
     * @return 导出结果
     */
    @Tool("导出学生列表")
    public String exportStudentList(@P("登录用户id") Long userId, @P("查询几条数据，如果没传默认为10条") Integer count) throws IOException {
        return "";
    }

    /**
     * 获取学生的最近运动记录
     * @param studentNumber 学号
     * @return 学生的最近运动记录
     */
    @Tool("获取学生最近的运动记录")
    public String getStudentSportRecord(@P("学生学号") String studentNumber) throws IOException {
        return "";
    }

    /**
     * 获取学生关注的圈子
     * @param studentNumber 学号
     * @return 学生的关注圈子
     */
    @Tool("获取学生关注的圈子")
    public String getStudentCircle(@P("学生学号") String studentNumber) throws IOException {
        return "";
    }

    /**
     * 获取学生关注的用户
     * @param studentNumber 学号
     * @return 学生的关注用户
     */
    @Tool("获取学生关注的用户")
    public String getStudentAttention(@P("学生学号") String studentNumber) throws IOException {
        return "";
    }

    /**
     * 获取用户点赞的文章
     * @param studentNumber 学号
     * @return 用户点赞的文章
     */
    @Tool("获取用户点赞的文章")
    public String getStudentLike(@P("学生学号") String studentNumber) throws IOException {
        return "";
    }
    /**
     * 调用另外一个模型使用mcp爬取网页
     *
     * @param url 网址
     * @return 网页内容
     */
    @Tool("爬取网页数据链接数据")
    public String crawlerWeb(@P("网址") String url) throws IOException {
        return assistantTeacherMCP.chat("456", "爬取网页数据" + url);
    }
    /**
     * 百度地图测试工具
     */
    @Tool("""
            该工具是一个地图的智能体，具有以下功能，提供全场景覆盖的地理信息服务，
            包括地理编码、逆地理编码、IP定位、天气查询、骑行路径规划、步行路径规划、驾车路径规划、
            公交路径规划、距离测量、关键词搜索、周边搜索、详情搜索等。当需要上述功能时直接传入问题
            """)
    public String baiDuMCP(@P("问题描述") String question) throws IOException {
        // 处理可能包含换行符的输入
        String processedQuestion = JsonSafeUtil.toSingleLine(question);
        return assistantTeacherMCP.chat("456", processedQuestion);
    }

    /**
     * 联网搜搜功能
     */
//    @Tool("联网搜索功能")
//    public String searchOnInternet(@P("需要搜索的内容") String content) throws IOException {
//        //这里是使用搜索引擎
//        WebSearchResults search = searchEngine.search(content);
//        return this.format(search);
//    }



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


    /**
     * 格式化学生心理状态
     *
     * @param mentalAnalysisResult 心理状态
     * @return 学生心理状态字符串
     */
    private String formatStudentMentalAnalysis(MentalAnalysisResult mentalAnalysisResult) {
        return "心理健康评价文本：" + mentalAnalysisResult.getSummary()
                + "，心理健康指标评分体系(0-10分，数值越高越健康)：" + mentalAnalysisResult.getMetrics()
                + "，关键字：" + mentalAnalysisResult.getKeywords();
    }


    /**
     * 联网搜索引擎格式化工具
     *
     * @param results 搜索引擎返回数据
     * @return 字符串结果
     */
    private String format(WebSearchResults results) {
        return Utils.isNullOrEmpty(results.results()) ? "No results found." : (String) results.results().stream().map((organicResult) -> {
            String var10000 = organicResult.title();
            return "Title: " + var10000 + "\nSource: " + organicResult.url().toString() + "\n" + (organicResult.content() != null ? "Content:\n" + organicResult.content() : "Snippet:\n" + organicResult.snippet());
        }).collect(Collectors.joining("\n\n"));
    }


    /**
     * 测评记录格式化工具
     *
     * @param historyByUserId 测评记录
     * @return 测评记录字符串
     */
    private List<Map<String, Object>> textHistoryFormate(List<HistoryTest> historyByUserId) {
        List<Map<String, Object>> list = new ArrayList<>();
        for (HistoryTest historyTest : historyByUserId) {
            Map<String, Object> map = new HashMap<>();
            Modules testTypeById = modulesService.getTestTypeById(historyTest.getModulesId());
            map.put("测评时间", historyTest.getCreateTime());
            map.put("测评结果", historyTest.getTestDescription());
            map.put("测评名称", testTypeById.getTitle());
            map.put("测评介绍", testTypeById.getIntroductions());
            map.put("测评描述", testTypeById.getResultDescription());
            list.add(map);
        }
        return list;
    }
}
