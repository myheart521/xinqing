package com.hpu.xinqing.service.ai;

import com.hpu.xinqingpojo.DTO.TempAlertLogsDTO;
import com.hpu.xinqingpojo.DTO.TempAlertUserDataDTO;
import com.hpu.xinqingpojo.DTO.TempEmotionRecordsDTO;
import com.hpu.xinqingpojo.DTO.TempEmotionUserDataDTO;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

import java.util.List;

public interface AssistantWaring {

    @SystemMessage("你是一个情绪识别助手，你可以基于用户传递的：发布的动态、参与的活动、发布的评论、与AI的对话分析出用户的心理状态")
    @UserMessage("""
            请根据输入内容{{tempEmotionUserDataList}}，分析并输出如下格式的 JSON 数组，每个元素为一个学生的情感分析结果，字段要求如下：
            [
              {
                "userId": 学生id（整数，如 12345）,
                "emotionType": "情感类型（字符串，取值为：快乐、愤怒、悲伤、恐惧、厌恶、惊讶、好）",
                "confidence": 情感置信度（0.0~1.0 的小数，如 0.85）,
                "featureBlogId": 分析出情感类型最具代表的博客id（长整型，如 1234567890）,
                "featureActiveId": 分析出情感类型最具代表的评论id（长整型，如 987654321）,
                "relatedContent": "分析出情感类型最具代表的评论内容或者与AI聊天内容（字符串）",
                "isAbnormal": 是否异常（整数，0表示正常，1表示异常）
              }
              // 可以有多个这样的对象
            ]
            请严格按照上述 JSON 数组格式输出，不要输出多余内容。
            """)
    String analyzeMotion(List<TempEmotionUserDataDTO> tempEmotionUserDataList);

    @SystemMessage("你是一个心理预警助手，你可以基于用户传递的数据分析出用户心理是否危机")
    @UserMessage("""
            请根据输入内容{{tempAlertUserDataDTOS}}，分析并以 JSON 数组形式输出报警记录列表，并且返回的列表emotionRecordId和userId要和{{tempAlertUserDataDTOS}}一一对应，每个对象的字段要求如下：
            [
              {
                "emotionRecordId": 关联的情感记录表id（长整型，如 1234567890）,
                "userId": 学生id（整数，如 1001）,
                "alertLevel": 报警等级（整数，0-低级，1-中级，2-紧急，3-正常 如 1）
                "recentBehavior": "最近的行为特征（字符串，如 "频繁发布动态","消极言论"）",
                "suggestion": "建议的处理方式（字符串，如 "关心谈话" "通知家长" "了解情况及时处理"）"
              }
              // 可以有多个这样的对象
            ]
            请严格按照上述 JSON 数组格式输出，所有字段必须完整且与说明一致，返回对应的数据不能为空，不要输出多余内容。
            """)
    String isWaring(List<TempAlertUserDataDTO> tempAlertUserDataDTOS);
}
