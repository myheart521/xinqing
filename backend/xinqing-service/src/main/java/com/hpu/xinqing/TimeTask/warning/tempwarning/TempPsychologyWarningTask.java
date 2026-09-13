package com.hpu.xinqing.TimeTask.warning.tempwarning;

import cn.hutool.core.util.ObjectUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hpu.xinqing.service.IActivityService;
import com.hpu.xinqing.service.ICommentsService;
import com.hpu.xinqing.service.ITempStudentService;
import com.hpu.xinqing.service.TempAlertLogsService;
import com.hpu.xinqing.service.TempEmotionRecordsService;
import com.hpu.xinqing.service.UserService;
import com.hpu.xinqing.service.ai.AssistantWaring;
import com.hpu.xinqing.service.ai.IAiChatMessageService;
import com.hpu.xinqingcommon.exception.AnalysisException;
import com.hpu.xinqingcommon.utils.MailUtil;
import com.hpu.xinqingpojo.DTO.ActiveAnalyseDTO;
import com.hpu.xinqingpojo.DTO.BlogAnalyseDTO;
import com.hpu.xinqingpojo.DTO.ChatWithAIAnalyseDTO;
import com.hpu.xinqingpojo.DTO.CommentAnalyseDTO;
import com.hpu.xinqingpojo.DTO.PsychologyWarningPredictionDTO;
import com.hpu.xinqingpojo.DTO.TempEmotionUserDataDTO;
import com.hpu.xinqingpojo.enmus.RoleEnums;
import com.hpu.xinqingpojo.entity.TempAlertLogs;
import com.hpu.xinqingpojo.entity.TempEmotionRecords;
import com.hpu.xinqingpojo.entity.TempStudent;
import com.hpu.xinqingpojo.entity.User;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
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
    private MentalWarningPolicy mentalWarningPolicy;

    @Value("${timeTask.warning.fixedDelay}")
    private long fixedDelay;

    private Map<Long, User> teacherMap;

    private Map<Long, User> studentMap;

    private final String title = "织心-心理预警提示：您有学生存在一定心理风险，请及时关注";

    private static final ScheduledExecutorService scheduledExecutor = new ScheduledThreadPoolExecutor(10);

    @Scheduled(cron = "0 0 2 * * ?")
    public void init() {
        log.info("临时的------心理预警服务启动中.....STARTING");
        new Thread(() -> {
            try {
                List<TempEmotionUserDataDTO> studentInfo = getStudentInfo();
                log.info("studentInfo size={}", studentInfo.size());
                startAnalyse(studentInfo);
            } catch (Exception e) {
                log.error("心理预警任务执行失败", e);
            }
        }).start();
        log.info("心理预警服务启动完成......SUCCESS");
    }

    private long computeDelay(LocalDateTime createTime) {
        long registerTimeMils = createTime.atZone(ZoneId.of("Asia/Shanghai")).toInstant().toEpochMilli();
        return registerTimeMils % fixedDelay;
    }

    private void startAnalyse(List<TempEmotionUserDataDTO> emotionUserData) {
        if (ObjectUtil.isEmpty(emotionUserData)) {
            return;
        }

        String predictionText = assistantWaring.analyzeMotion(emotionUserData);
        List<PsychologyWarningPredictionDTO> predictions = parsePredictions(predictionText);
        if (ObjectUtil.isEmpty(predictions)) {
            return;
        }

        Map<Long, TempEmotionUserDataDTO> sourceMap = emotionUserData.stream()
                .filter(item -> item.getId() != null)
                .collect(Collectors.toMap(TempEmotionUserDataDTO::getId, item -> item, (left, right) -> left));

        LocalDateTime now = LocalDateTime.now();
        List<TempEmotionRecords> emotionRecords = new ArrayList<>();
        for (PsychologyWarningPredictionDTO prediction : predictions) {
            TempEmotionUserDataDTO source = sourceMap.get(prediction.getUserId());
            if (source == null) {
                log.warn("未找到 userId={} 对应的源数据，已跳过", prediction.getUserId());
                continue;
            }
            emotionRecords.add(buildEmotionRecord(source, prediction, now));
        }

        if (ObjectUtil.isEmpty(emotionRecords)) {
            return;
        }

        emotionRecordsService.saveBatch(emotionRecords);

        Map<Long, TempEmotionRecords> emotionRecordMap = emotionRecords.stream()
                .filter(item -> item.getUserId() != null)
                .collect(Collectors.toMap(TempEmotionRecords::getUserId, item -> item, (left, right) -> left));

        List<TempAlertLogs> alertLogs = new ArrayList<>();
        for (PsychologyWarningPredictionDTO prediction : predictions) {
            TempEmotionUserDataDTO source = sourceMap.get(prediction.getUserId());
            TempEmotionRecords emotionRecord = emotionRecordMap.get(prediction.getUserId());
            if (source == null || emotionRecord == null || emotionRecord.getId() == null) {
                continue;
            }
            alertLogs.add(buildAlertLog(source, emotionRecord, prediction, now));
        }

        if (ObjectUtil.isNotEmpty(alertLogs)) {
            tempAlertLogsService.saveBatch(alertLogs);
            dealWaring(alertLogs);
        }
    }

    private List<PsychologyWarningPredictionDTO> parsePredictions(String rawText) {
        if (rawText == null || rawText.isBlank()) {
            throw new AnalysisException("AI预警结果为空");
        }
        String payload = extractJsonPayload(rawText);
        try {
            ObjectMapper lenientMapper = new ObjectMapper()
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            return lenientMapper.readValue(payload, new TypeReference<List<PsychologyWarningPredictionDTO>>() {
            });
        } catch (Exception e) {
            throw new AnalysisException("AI预警结果解析失败:" + e.getMessage());
        }
    }

    static String extractJsonPayload(String rawText) {
        String trimmed = rawText.trim();
        int fencedStart = trimmed.indexOf("```");
        if (fencedStart >= 0) {
            int fenceBodyStart = trimmed.indexOf('\n', fencedStart);
            int fenceBodyEnd = trimmed.lastIndexOf("```");
            if (fenceBodyStart >= 0 && fenceBodyEnd > fenceBodyStart) {
                return trimmed.substring(fenceBodyStart + 1, fenceBodyEnd).trim();
            }
        }

        int firstBracket = trimmed.indexOf('[');
        int lastBracket = trimmed.lastIndexOf(']');
        if (firstBracket >= 0 && lastBracket > firstBracket) {
            return trimmed.substring(firstBracket, lastBracket + 1).trim();
        }

        int firstBrace = trimmed.indexOf('{');
        int lastBrace = trimmed.lastIndexOf('}');
        if (firstBrace >= 0 && lastBrace > firstBrace) {
            return trimmed.substring(firstBrace, lastBrace + 1).trim();
        }

        return trimmed;
    }

    private TempEmotionRecords buildEmotionRecord(TempEmotionUserDataDTO source,
                                                  PsychologyWarningPredictionDTO prediction,
                                                  LocalDateTime now) {
        TempEmotionRecords record = new TempEmotionRecords();
        record.setUserId(source.getId());
        record.setDetectionTime(now);
        record.setEmotionType(prediction.getEmotionLabel());
        record.setConfidence(prediction.getEmotionConfidence());
        record.setFeatureBlogId(mentalWarningPolicy.selectFeatureBlogId(source));
        record.setFeatureActiveId(mentalWarningPolicy.selectFeatureActiveId(source));
        record.setRelatedContent(mentalWarningPolicy.buildRelatedContent(source));
        record.setIsAbnormal(mentalWarningPolicy.isAbnormal(prediction));
        return record;
    }

    private TempAlertLogs buildAlertLog(TempEmotionUserDataDTO source,
                                        TempEmotionRecords emotionRecord,
                                        PsychologyWarningPredictionDTO prediction,
                                        LocalDateTime now) {
        int alertLevel = mentalWarningPolicy.mapAlertLevel(prediction);
        TempAlertLogs alertLog = new TempAlertLogs();
        alertLog.setEmotionRecordId(emotionRecord.getId());
        alertLog.setUserId(source.getId());
        alertLog.setTriggerTime(now);
        alertLog.setAlertLevel(alertLevel);
        alertLog.setHandlingStatus(mentalWarningPolicy.resolveHandlingStatus(alertLevel));
        alertLog.setHandleWay(mentalWarningPolicy.resolveHandleWay(alertLevel));
        alertLog.setIsResolve(mentalWarningPolicy.resolveIsResolve(alertLevel));
        alertLog.setRecentBehavior(mentalWarningPolicy.buildRecentBehavior(prediction, source));
        alertLog.setSuggestion(mentalWarningPolicy.buildSuggestion(prediction, alertLevel));
        alertLog.setNote(alertLevel == 3 ? "系统自动判定为正常状态" : "系统自动生成预警");
        return alertLog;
    }

    private void dealWaring(List<TempAlertLogs> tempAlertLogs) {
        studentMap = userService.lambdaQuery()
                .eq(User::getRoleId, RoleEnums.STUDENT.getType())
                .eq(User::getIsDelete, 0)
                .list()
                .stream()
                .collect(Collectors.toMap(User::getId, user -> user, (left, right) -> left));

        teacherMap = userService.lambdaQuery()
                .eq(User::getRoleId, RoleEnums.TEACHER.getType())
                .eq(User::getIsDelete, 0)
                .list()
                .stream()
                .collect(Collectors.toMap(User::getId, user -> user, (left, right) -> left));

        List<TempStudent> tempStudentList = new ArrayList<>();
        for (TempAlertLogs tempAlertLog : tempAlertLogs) {
            Integer alertLevel = tempAlertLog.getAlertLevel();
            Long userId = tempAlertLog.getUserId();
            if (alertLevel == null || userId == null) {
                continue;
            }

            User student = studentMap.get(userId);
            if (student == null) {
                continue;
            }

            int scoreDelta = mentalWarningPolicy.scoreDelta(alertLevel);
            if (scoreDelta != 0) {
                userService.updateScoreByUserId(userId, scoreDelta);
            }

            if (alertLevel == 3) {
                continue;
            }

            TempStudent tempStudent = new TempStudent();
            tempStudent.setStudentId(userId);
            tempStudent.setTeacherId(student.getTeacherId());
            tempStudent.setSuggestion(tempAlertLog.getSuggestion());
            tempStudent.setAlertLevel(alertLevel);
            tempStudent.setStatus(0);
            tempStudent.setCreateTime(LocalDateTime.now());
            tempStudent.setUpdateTime(LocalDateTime.now());
            tempStudentList.add(tempStudent);

            if (mentalWarningPolicy.shouldNotifyTeacher(alertLevel)) {
                String email = getTeacherEmail(userId);
                if (email != null && !email.isBlank()) {
                    MailUtil.sendWarningEmail(email, title, buildEmailContent(student, tempAlertLog));
                }
            }
        }

        if (ObjectUtil.isNotEmpty(tempStudentList)) {
            tempStudentService.saveBatch(tempStudentList);
        }
    }

    private String buildEmailContent(User student, TempAlertLogs tempAlertLog) {
        return """
                检测到学生近期存在心理预警信号，请及时关注。
                
                学生姓名：%s
                学号：%s
                预警级别：%s
                最近行为特征：%s
                建议处理方式：%s
                """.formatted(student.getUserName(),
                student.getUserAccount(),
                warningLevelText(tempAlertLog.getAlertLevel()),
                tempAlertLog.getRecentBehavior(),
                tempAlertLog.getSuggestion());
    }

    private String warningLevelText(Integer alertLevel) {
        if (alertLevel == null) {
            return "未知";
        }
        return switch (alertLevel) {
            case 0 -> "低级";
            case 1 -> "中级";
            case 2 -> "紧急";
            case 3 -> "正常";
            default -> "未知";
        };
    }

    private String getTeacherEmail(Long userId) {
        User student = studentMap.get(userId);
        if (student == null || student.getTeacherId() == null) {
            log.error("用户:{} 没有绑定辅导员老师", userId);
            return null;
        }
        User teacher = teacherMap.get(student.getTeacherId());
        if (teacher == null) {
            log.error("用户:{} 对应的辅导员老师不存在", userId);
            return null;
        }
        return teacher.getEmail();
    }

    private List<TempEmotionUserDataDTO> getStudentInfo() {
        List<User> studentInfo = userService.getStudentInfo();
        List<TempEmotionUserDataDTO> tempEmotionUserData = new ArrayList<>();
        studentInfo.forEach(user -> {
            TempEmotionUserDataDTO data = new TempEmotionUserDataDTO();
            data.setId(user.getId());

            List<BlogAnalyseDTO> blogByUserId = userService.getBlogByUserId(user.getId());
            if (ObjectUtil.isNotEmpty(blogByUserId)) {
                data.setBlogAnalyseList(blogByUserId);
            }

            List<ActiveAnalyseDTO> activityByUserId = userService.getActivityByUserId(user.getId());
            if (ObjectUtil.isNotEmpty(activityByUserId)) {
                data.setActiveAnalyseList(activityByUserId);
            }

            List<CommentAnalyseDTO> commentAnalyseDTOS = commentsService.queryCommentRecentByUserId(user.getId());
            if (ObjectUtil.isNotEmpty(commentAnalyseDTOS)) {
                data.setNoteAnalyseList(commentAnalyseDTOS);
            }

            List<ChatWithAIAnalyseDTO> chatWithAIAnalyseDTOS = aiChatMessageService.selectAnalyseByUserId(user.getId());
            if (ObjectUtil.isNotEmpty(chatWithAIAnalyseDTOS)) {
                data.setChatWithAIAnalyseList(chatWithAIAnalyseDTOS);
            }
            tempEmotionUserData.add(data);
        });
        return tempEmotionUserData;
    }
}
