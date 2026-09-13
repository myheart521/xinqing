package com.hpu.xinqing.TimeTask.warning.tempwarning;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.util.ObjectUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hpu.xinqing.service.*;
import com.hpu.xinqing.service.ai.AssistantWaring;
import com.hpu.xinqing.service.ai.IAiChatMessageService;
import com.hpu.xinqing.service.serviceImpl.BlogServiceImpl;
import com.hpu.xinqingcommon.utils.MailUtil;
import com.hpu.xinqingpojo.DTO.*;
import com.hpu.xinqingpojo.enmus.RoleEnums;
import com.hpu.xinqingpojo.entity.*;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.stream.Collectors;

@Component
@Slf4j
@Transactional
public class TempPsychologyWarningTask {
    @Resource
    private UserService userService;

    @Resource
    private ICommentsService commentsService;

    @Resource
    private IAiChatMessageService aiChatMessageService;

    @Resource
    private AssistantWaring assistantWaring;

    @Resource
    private TempEmotionRecordsService emotionRecordsService;

    @Resource
    private TempAlertLogsService tempAlertLogsService;

    @Resource
    private ITempStudentService tempStudentService;

    @Resource
    private IActivityService activityService;

    @Value("${timeTask.warning.fixedDelay}")
    private long fixedDelay;

    private Long studentCount;

    private Long currentCount;

    private Map<Long, User> teacherMap;

    private Map<Long, User> studentMap;

    private String title = "织心-心理预警提示：" +
            "您有学生存在一定心理问题，请及时关注";

    private static final ScheduledExecutorService scheduledExecutor = new ScheduledThreadPoolExecutor(10);
    @Autowired
    private BlogServiceImpl blogServiceImpl;

    @Scheduled(cron = "0 0 2 * * ?")
    public void init() throws InterruptedException {
        log.info("临时的-------心理预警服务启动中......STARTING");
        //1.查询学生的各种信息
        //3. 处理结果通知
        new Thread(() -> {
            try {
                List<TempEmotionUserDataDTO> studentInfo = getStudentInfo();
                System.out.println("studentInfo:" + studentInfo);
                startAnalyse(studentInfo);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }).start();
        log.info("心理预警服务启动完成......SUCCESS");
    }

    //计算第一次执行的时间
    private long computeDelay(LocalDateTime createTime) {
        long registerTimeMils = createTime.atZone(ZoneId.of("Asia/Shanghai")).toInstant().toEpochMilli();
        return registerTimeMils % fixedDelay;
    }

    /**
     * 开始分析
     */
    private void startAnalyse(List<TempEmotionUserDataDTO> emotionUserData) throws JsonProcessingException {

        //先分析出情感记录
        String tempAnalyseResultStr = assistantWaring.analyzeMotion(emotionUserData);

        System.out.println("tempAnalyseResultStr：" + tempAnalyseResultStr);
        // 创建 ObjectMapper 实例
        ObjectMapper objectMapper = new ObjectMapper();

        // 转换为 List<TempEmotionRecordsDTO>
        List<TempEmotionRecordsDTO> list = objectMapper.readValue(
                tempAnalyseResultStr,
                new TypeReference<List<TempEmotionRecordsDTO>>() {
                }
        );
        List<TempEmotionRecords> emotionRecords = BeanUtil.copyToList(list, TempEmotionRecords.class);
        if (ObjectUtil.isNotEmpty(emotionRecords)) {
            //操纵数据库插入
            emotionRecordsService.saveBatch(emotionRecords);
            //然后根据分析出的情感记录分析是否需要心理预警
            List<TempAlertUserDataDTO> tempAlertUserDataDTOList = new ArrayList<>();
            emotionRecords.forEach(emotion -> {
                TempAlertUserDataDTO data = BeanUtil.copyProperties(emotion, TempAlertUserDataDTO.class);
                data.setEmotionRecordId(emotion.getId());
                data.setUserId(emotion.getUserId());
                //获取博客
                Blog blog = blogServiceImpl.getById(emotion.getFeatureBlogId());
                data.setFeatureBlog(blog);
                //获取活动
                Activity activity = activityService.getById(emotion.getFeatureActiveId());
                data.setFeatureActive(activity);
                tempAlertUserDataDTOList.add(data);
            });
            String waring = assistantWaring.isWaring(tempAlertUserDataDTOList);
            // 转换为 List<TempEmotionRecordsDTO>
            List<TempAlertLogsDTO> waringList = objectMapper.readValue(
                    waring,
                    new TypeReference<List<TempAlertLogsDTO>>() {
                    }
            );
            if (ObjectUtil.isNotEmpty(waringList)) {
                List<TempAlertLogs> tempAlertLogs = BeanUtil.copyToList(waringList, TempAlertLogs.class, CopyOptions.create()
                        .ignoreNullValue());
                //这里插入数据库
                tempAlertLogsService.saveBatch(tempAlertLogs);
                dealWaring(tempAlertLogs);
            }
        }
    }

    /**
     * 处理心理预警
     *
     * @param tempAlertLogs
     */

    private void dealWaring(List<TempAlertLogs> tempAlertLogs) {
        this.studentMap = userService.lambdaQuery().eq(User::getRoleId, RoleEnums.STUDENT.getType())
                .eq(User::getIsDelete, 0)
                .list().stream()
                .collect(Collectors.toMap(User::getId, user -> user));
        this.teacherMap = userService.lambdaQuery().eq(User::getRoleId, RoleEnums.TEACHER.getType())
                .eq(User::getIsDelete, 0)
                .list().stream()
                .collect(Collectors.toMap(User::getId, user -> user));
        List<TempStudent> tempStudentList=new ArrayList<>();
        //查询用户的心理预警等级
        for (TempAlertLogs tempAlertLog : tempAlertLogs) {
            Integer alertLevel = tempAlertLog.getAlertLevel();
            Long userId = tempAlertLog.getUserId();
            User student = studentMap.get(userId);
            if(alertLevel== null) continue;
            if(alertLevel==0){
                continue;
            }else if(alertLevel==1) {
                Integer score=student.getScore();
                if(student.getScore()>10){
                    User user = userService.updateScoreByUserId(userId, -10);
                    score = user.getScore();
                }

                if(score<=50){
                    //说明同学已经连续心理风险一段时间了
                   String content = """
                      对用户近期的行为特征进行分析，发现该用户存在心理问题的风险。
                      并且可能已经持续一段时间了，建议及时处理。
                      学生信息：
                      学生姓名：%s
                      学号：%s
                      心理评分：%s
                      """.formatted(student.getUserName(),student.getUserAccount(),score);
                    String email = getTeacherEmail(userId);
                    MailUtil.sendWarningEmail(email, title, content);
                    TempStudent tempStudent=new TempStudent();
                    tempStudent.setStudentId(userId);
                    tempStudent.setSuggestion(tempAlertLog.getSuggestion());
                    tempStudent.setAlertLevel(1);
                    tempStudentList.add(tempStudent);
                    log.info(content);
                }
            }else if(alertLevel==3){
                log.info("用户：{} ，心理健康已经好转",userId);
                User user = userService.updateScoreByUserId(userId, 10);
            }
            else if (alertLevel == 2) {
                String content ="""
                通过对用户一周的行为特征进行分析，发现该用户存在心理问题的风险
                预警详情：
                学生姓名：%s
                学号：%s
                预警级别：%s
                最近行为特征：%s
                建议处理方式：%s
                """;
                //发送邮件
                String email = getTeacherEmail(userId);
                content = content.formatted(student.getUserName(),
                        student.getUserAccount(), "高危",
                        tempAlertLog.getRecentBehavior(),
                        tempAlertLog.getSuggestion());
                MailUtil.sendWarningEmail(email, title, content);
                TempStudent tempStudent=new TempStudent();
                tempStudent.setStudentId(userId);
                tempStudent.setSuggestion(tempAlertLog.getSuggestion());
                tempStudent.setAlertLevel(2);
                tempStudentList.add(tempStudent);
                log.info(content);
            }
        }
    }

    /**
     * 发邮件
     */
    private String getTeacherEmail(Long userId) {
        User student = studentMap.get(userId);
        Long teacherId = student.getTeacherId();
        if (teacherId == null) {
            log.error("用户:{},没有绑带辅导员老师", userId);
            return null;
        }
        User teacher = teacherMap.get(teacherId);
        if (teacher == null) {
            log.error("用户：{},但是对应的辅导员老师是错误的", userId);
            return null;
        }
       return teacher.getEmail();
    }


    /**
     * TODO 获取所有学生的信息，这里之后再完善，现在学生人数少所以没事
     *
     * @return 学生信息
     */
    private List<TempEmotionUserDataDTO> getStudentInfo() {
        //获取学生列表
        List<User> studentInfo = userService.getStudentInfo();
        List<TempEmotionUserDataDTO> tempEmotionUserData = new ArrayList<>();
        studentInfo.forEach(user -> {
            TempEmotionUserDataDTO data = new TempEmotionUserDataDTO();
            //学生id
            data.setId(user.getId());
            //博客，最近的三条
            List<BlogAnalyseDTO> blogByUserId = userService.getBlogByUserId(user.getId());
            if (ObjectUtil.isNotEmpty(blogByUserId)) {
                data.setBlogAnalyseList(blogByUserId);
            }
            //活动，最近的3条
            List<ActiveAnalyseDTO> activityByUserId = userService.getActivityByUserId(user.getId());
            if (ObjectUtil.isNotEmpty(activityByUserId)) {
                data.setActiveAnalyseList(activityByUserId);
            }
            //评论，一周的
            List<CommentAnalyseDTO> commentAnalyseDTOS = commentsService.queryCommentRecentByUserId(user.getId());
            if (ObjectUtil.isNotEmpty(commentAnalyseDTOS)) {
                data.setNoteAnalyseList(commentAnalyseDTOS);
            }
            //与AI的对话，一周内的
            List<ChatWithAIAnalyseDTO> chatWithAIAnalyseDTOS = aiChatMessageService.selectAnalyseByUserId(user.getId());
            if (ObjectUtil.isNotEmpty(chatWithAIAnalyseDTOS)) {
                data.setChatWithAIAnalyseList(chatWithAIAnalyseDTOS);
            }
            tempEmotionUserData.add(data);

        });
        return tempEmotionUserData;
    }
}
