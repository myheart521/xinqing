package com.hpu.xinqing.service.ai;

import com.hpu.xinqingpojo.DTO.TempAlertLogsDTO;
import com.hpu.xinqingpojo.DTO.TempAlertUserDataDTO;
import com.hpu.xinqingpojo.DTO.TempEmotionUserDataDTO;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

import java.util.List;

public interface AssistantWaring {

    @SystemMessage("""
            你是心理预警分析助手，只负责输出严格的 JSON 数组。
            你的输出必须与接口字段保持一致，不要输出解释、注释、Markdown 代码块或多余文本。
            """)
    @UserMessage("""
            请根据输入内容 {tempEmotionUserDataList} 分析每个学生的情绪与风险，并输出 JSON 数组。
            每个数组元素都必须包含以下字段：
            [
              {
                "userId": 学生id（整数）,
                "emotion_label": "情绪标签，尽量使用中文，如 焦虑、抑郁、无助、羞耻、疲惫、愤怒、平静",
                "emotion_confidence": 0.0~1.0 的小数,
                "risk_level": 0~4 的整数，其中 0=正常，1=低风险，2=中风险，3=高风险，4=极高风险,
                "risk_confidence": 0.0~1.0 的小数,
                "risk_probs": {
                  "0": 0.0~1.0,
                  "1": 0.0~1.0,
                  "2": 0.0~1.0,
                  "3": 0.0~1.0,
                  "4": 0.0~1.0
                }
              }
            ]
            要求：
            1. 只输出 JSON 数组。
            2. 每个学生必须输出一个对象，且 userId 必须与输入一一对应。
            3. risk_probs 必须包含 0、1、2、3、4 五个键。
            4. 不要输出多余说明，不要使用 Markdown 代码块。
            """)
    String analyzeMotion(List<TempEmotionUserDataDTO> tempEmotionUserDataList);

    @SystemMessage("你是心理预警建议助手，根据输入数据生成预警处置建议，只输出 JSON 数组。")
    @UserMessage("""
            请根据输入内容 {tempAlertUserDataDTOS} 分析并输出 JSON 数组。
            每个数组元素都必须包含以下字段：
            [
              {
                "emotionRecordId": 关联的情感记录表 id（整数）,
                "userId": 学生id（整数）,
                "alertLevel": 0~3 的整数，其中 0=低级，1=中级，2=紧急，3=正常,
                "recentBehavior": "最近的行为特征，需简洁具体",
                "suggestion": "建议的处置方式，需简洁具体"
              }
            ]
            要求：
            1. 只输出 JSON 数组。
            2. 每条记录都必须与输入中的 emotionRecordId 和 userId 一一对应。
            3. 不要输出多余说明，不要使用 Markdown 代码块。
            """)
    String isWaring(List<TempAlertUserDataDTO> tempAlertUserDataDTOS);
}
