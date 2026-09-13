package com.hpu.xinqing.TimeTask.warning;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.hpu.xinqing.service.ICommentsService;
import com.hpu.xinqing.service.IStudentService;
import com.hpu.xinqing.service.UserService;
import com.hpu.xinqing.service.ai.IAiChatMemoryService;
import com.hpu.xinqing.service.ai.IAiChatMessageService;
import com.hpu.xinqing.utils.common.ListUtil;
import com.hpu.xinqingcommon.constant.RedisConstant;
import com.hpu.xinqingcommon.result.EmoResult;
import com.hpu.xinqingpojo.entity.*;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.testcontainers.shaded.org.checkerframework.checker.units.qual.A;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
public class PsychologyWarningCoreTask implements PsychologyWarningCoreTaskHandler {

    @Autowired
    private UserService userService;
    @Autowired
    private IAiChatMemoryService aiChatMemoryService;
    @Autowired
    private ICommentsService commentsService;
    @Autowired
    private IAiChatMessageService aiChatMessageService;
    @Autowired
    private IStudentService studentService;

    @Override
    public PsychologyWarningTask.InvokeRes handleTask(Long userId) {
        LocalDate now = LocalDate.now().plusDays(-1);
        LocalDateTime startTime = now.atTime(0, 0, 0);
        LocalDateTime endTime = now.atTime(23, 59, 59);
        //查询学生用户的聊天状态
        User user = userService.getById(userId);
        String school=user.getSchool();
        List<String> list=new ArrayList<>();
        //查询用户所有的回话信息
        List<AiChatMemory> memoryList = aiChatMemoryService.lambdaQuery()
                .eq(AiChatMemory::getUserId, userId)
                .list();
        if (ListUtil.isNotEmpty(memoryList)) {
            List<String> mermoryIds = memoryList.stream().map(AiChatMemory::getId).collect(Collectors.toList());
            List<AiChatMessage> messagesList = aiChatMessageService.lambdaQuery()
                    .in(AiChatMessage::getMemoryId, mermoryIds)
                    .eq(AiChatMessage::getType, "USER")
                    .between(AiChatMessage::getCreateTime, startTime, endTime)
                    .list();
            if(ListUtil.isNotEmpty(messagesList)){
                List<String> messageCollect = messagesList.stream().map(AiChatMessage::getContent).collect(Collectors.toList());
                list.addAll(messageCollect);
            }
        }
        //查询评论信息
        List<Comments> commentList = commentsService.lambdaQuery().eq(Comments::getUserId, userId)
                .between(Comments::getCreateTime, startTime, endTime)
                .list();
        if(ListUtil.isNotEmpty(commentList)){
            List<String> commentCollect = commentList.stream().map(Comments::getContent).collect(Collectors.toList());
            list.addAll(commentCollect);

        }
        if(list.size()==0){
            PsychologyWarningTask.InvokeRes invokeRes= PsychologyWarningTask.InvokeRes.failure(userId);
            return invokeRes;
        }
        //调用AI
        List<EmoResult> emoResults = postAI(list, userId);
        if(emoResults.size()<=10){
           PsychologyWarningTask.InvokeRes invokeRes= PsychologyWarningTask.InvokeRes.failure(userId);
            return invokeRes;
        }
        int count=0;
        for (EmoResult emoResult : emoResults) {
            if(emoResult.getLabel().equals("LABEL_0")){
                count++;
            }
        }
        double score = count * 1.0 / emoResults.size();
        PsychologyWarningTask.TeskResult teskResult = new PsychologyWarningTask.TeskResult(userId, school, 0, user.getStudentNumber(), "", now);
        if (score > 0.9) {
            String s = now + "学号为：" + user.getStudentNumber() + "，姓名："
                    + user.getUserName() + "同学用AI聊天消极情绪比例为：" + score + "，请及时处理";
            teskResult.setContent(s);
            return getInvokeRes(-20,teskResult);
        }else if(score>0.8){
            String s = now + "学号为：" + user.getStudentNumber() + "，姓名："
                    + user.getUserName() + "同学用AI聊天消极情绪比例为：" + score + "，请及时处理";
            teskResult.setContent(s);
            return getInvokeRes(-10,teskResult);
        }else if(score>0.7) {
            String s = now + "学号为：" + user.getStudentNumber() + "，姓名："
                    + user.getUserName() + "同学用AI聊天消极情绪比例为：" + score + "，请及时处理";
            teskResult.setContent(s);
            return getInvokeRes(-5,teskResult);
        }
        return PsychologyWarningTask.InvokeRes.failure(userId);
    }

    @NotNull
    private PsychologyWarningTask.InvokeRes getInvokeRes(int score, PsychologyWarningTask.TeskResult teskResult) {
        Long userId = teskResult.getUserId();
        Student student = studentService.updateScoreByUserId(userId, score);
        if(student==null){
            log.error("更新学生分数失败");
            //TODO:不知道如何重新进行任务
            return PsychologyWarningTask.InvokeRes.failure(userId);
        }
        teskResult.setScore(student.getScore());
        return PsychologyWarningTask.InvokeRes.success(userId, teskResult);
    }
    public List<EmoResult> postAI(List<String> messageCollect,Long userId){
        Map<String, Object> questMap = new HashMap<>();
        questMap.put("input_data", messageCollect);
        questMap.put("task_id", userId.toString());
        String jsonStr = JSONUtil.toJsonStr(questMap);
        String url = "http://127.0.0.1:8001/batch-process";
        String result = HttpUtil.post(url, jsonStr);
        JSONObject rootObj = JSONUtil.parseObj(result);
        JSONArray resultsArray = rootObj.getJSONArray("results");
        List<EmoResult> results = JSONUtil.toList(resultsArray, EmoResult.class);
        results.removeIf(emoResult -> emoResult.getScore() < 0.6);
        return results;
    }



    @Override
    public boolean supports(String handlerName) {
        return handlerName.equals("qihe");
    }
}
