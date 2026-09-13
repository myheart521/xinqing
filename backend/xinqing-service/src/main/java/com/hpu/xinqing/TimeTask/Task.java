package com.hpu.xinqing.TimeTask;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.hpu.xinqing.mapper.EquipmentMapper;
import com.hpu.xinqing.service.*;
import com.hpu.xinqing.service.ai.IAiChatMemoryService;
import com.hpu.xinqing.service.ai.IAiChatMessageService;
import com.hpu.xinqingcommon.constant.RedisConstant;
import com.hpu.xinqingcommon.result.EmoResult;
import com.hpu.xinqingcommon.utils.HttpClientUtil;
import com.hpu.xinqingcommon.utils.MailUtil;
import com.hpu.xinqingpojo.entity.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class Task {

    private final IBodyService bodyService;
    private final IRunningService runningService;
    private final EquipmentMapper equipmentMapper;
    private final UserService userService;
    private final IAiChatMessageService aiChatMessageService;
    private final IAiChatMemoryService aiChatMemoryService;
    private final StringRedisTemplate stringRedisTemplate;
    private final IAppointmentService appointmentService;

//    private

    @Scheduled(cron = "0 0 1 * * ? ")//每天凌晨1点运行
    public void updateBody() {
        log.info("查询平台更新体脂秤数据");
        List<Equipment> equipmentList = equipmentMapper.selectByEquipType("body");
        for (Equipment equipment : equipmentList) {
            String equipId = equipment.getEquipId();
            String[] s = equipId.split("\\$");
            String id = s[1];
            bodyService.updateLoTDA(id);
        }
    }

    @Scheduled(cron = "0 0 1 * * ? ")//每天凌晨1点运行
    public void updateRunning() {
        log.info("查询平台更新跑步机数据");
        List<Equipment> equipmentList = equipmentMapper.selectByEquipType("running");
        for (Equipment equipment : equipmentList) {
            String equipId = equipment.getEquipId();
            String[] s = equipId.split("\\$");
            String id = s[1];
            runningService.updateLoTDA(id);
        }
    }

    @Scheduled(cron = "0 0 20 * * ? ")//每天凌晨1点运行
    public void updateStatus() {
        //TODO:每天8点给学生发信息，给老师信息
        LocalDate now = LocalDate.now();
        appointmentService.lambdaUpdate()
                .set(Appointment::getStatus, 3)
                .set(Appointment::getExcuse, "超时未及时处理，自动取消")
                .eq(Appointment::getDate, now)
                .update();
    }

    //每天8点给学生发信息，给老师信息
    @Scheduled(cron = "0 0 7 * * ? ")//每天凌晨1点运行
    public void sendMessage() {
        //TODO:每天8点给学生发信息，给老师信息
        LocalDate now = LocalDate.now();
        List<Appointment> list = appointmentService.lambdaQuery()
                .eq(Appointment::getDate, now)
                .in(Appointment::getStatus, Arrays.asList(0, 1))
                .list();
        if (list == null || list.size() == 0) {
            return;
        }
        List<Long> studentId = list.stream().map(Appointment::getStudentId).collect(Collectors.toList());
        Map<Long, User> studentMap = userService.lambdaQuery().in(User::getId, studentId).list()
                .stream().collect(Collectors.toMap(User::getId, item -> item));
        List<Long> teacherIds = list.stream().map(Appointment::getTeacherId).collect(Collectors.toList());
        Map<Long, User> teacherMap = userService.lambdaQuery().in(User::getId, teacherIds).list()
                .stream().collect(Collectors.toMap(User::getId, item -> item));
        HashMap<Long, count> map = new HashMap<>();
        String studentEmail= """
                亲爱的%s同学：\n
                您的今天有成功预约咨询，
                您的预约开始时间为%s，\n
                老师为%s，\n
                地点为%s，\n
                请注意准时参加。\n
                """;
        String teacherEmail= """
                    亲爱的%s老师：\n
                    今天您有%s位同学预约咨询，
                    其中成功预约%s位，\n
                    未处理%s位，\n
                    请及时处理。\n
                    """;
        for (Appointment appointment : list) {
            Long studentId1 = appointment.getStudentId();
            Long teacherId = appointment.getTeacherId();
            User student = studentMap.get(studentId1);
            User teacher = teacherMap.get(teacherId);
            String studentName = student.getUserName();
            String teacherName = teacher.getUserName();
            count count = map.get(teacherId);
            if(count==null){
                count=new count();
                count.normal=1;
                count.success=1;
                map.put(teacherId,count);
            }else {
                count.normal++;
                count.success++;
            }
            //发邮件
            MailUtil.sendEmail(student.getEmail(),"预约提醒",
                    studentEmail.formatted(studentName,appointment.getStartTime(),teacherName,"学校咨询中心"));

        }

        map.forEach((key,value)->{
            User teacher = teacherMap.get(key);
            MailUtil.sendEmail(teacher.getEmail(),"今日预约提醒",
                    teacherEmail.formatted(teacher.getUserName(),value.normal+value.success,value.success,value.normal));
        });

    }

    //需要定时检测用户的聊天状态，1天一次
    //如果出现非常消极的情况，给对应的老师发信息
//    @Scheduled(cron = "0 0 1 * * ? ")//每天凌晨1点运行
    public void checkUser() {
        //TODO:每天运行一次，检测用户的聊天状态，发布消息队列供第二天老师查看
        String key = RedisConstant.SCHOOL_STUDENT_WARNING;
        LocalDate now = LocalDate.now().plusDays(-1);
        LocalDateTime startTime = now.atTime(0, 0, 0);
        LocalDateTime endTime = now.atTime(23, 59, 59);
        //查询学生用户,根据学校分组
        List<User> userList = userService.lambdaQuery().eq(User::getRoleId, "3")
                .eq(User::getIsDelete, 0)
                .list();
        Map<String, List<User>> map = userList.stream().collect(Collectors.groupingBy(User::getSchool));
        //查询学生用户的聊天状态
        for (String school : map.keySet()) {
            List<User> users = map.get(school);
            String schoolKey = key + school;
            for (User user : users) {
                Long userId = user.getId();
                List<AiChatMemory> list = aiChatMemoryService.lambdaQuery().eq(AiChatMemory::getUserId, userId).list();
                if (list == null || list.size() == 0) {
                    continue;
                }
                List<String> mermoryIds = list.stream().map(AiChatMemory::getId).collect(Collectors.toList());
                List<AiChatMessage> messagesList = aiChatMessageService.lambdaQuery().in(AiChatMessage::getMemoryId, mermoryIds)
                        .eq(AiChatMessage::getType, "USER")
                        .between(AiChatMessage::getCreateTime, startTime, endTime)
                        .list();
                if (messagesList == null || messagesList.size() == 0) {
                    continue;
                }
                List<String> messageCollect = messagesList.stream().map(AiChatMessage::getContent).collect(Collectors.toList());
                Map<String, Object> questMap = new HashMap<>();
                questMap.put("input_data", messageCollect);
                questMap.put("task_id", userId.toString());
                String jsonStr = JSONUtil.toJsonStr(questMap);
                String url = "http://127.0.0.1:8001/batch-process";
                String result = HttpUtil.post(url, jsonStr);
                JSONObject rootObj = JSONUtil.parseObj(result);
                JSONArray resultsArray = rootObj.getJSONArray("results");
                int count = 0;
                int num = resultsArray.size();
                List<EmoResult> results = JSONUtil.toList(resultsArray, EmoResult.class);
                for (EmoResult emoResult : results) {
                    if (emoResult.getScore() > 0.5 && emoResult.getLabel().equals("LABEL_0")) {
                        count++;
                    }
                }
                double score = count * 1.0 / num;
                if (score > 0.7) {
                    //TODO:给对应的老师发信息
                    String s = now + "学号为：" + user.getStudentNumber() + "，姓名：" + user.getUserName() + "同学用AI聊天消极情绪比例高达：" + score + "，请及时处理";
                    stringRedisTemplate.opsForHash().put(schoolKey, userId.toString(), s);
                }
            }
        }
    }

    class count {
        int normal;
        int success;
    }
}


