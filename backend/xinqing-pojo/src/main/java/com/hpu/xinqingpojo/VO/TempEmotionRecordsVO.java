package com.hpu.xinqingpojo.VO;

import com.hpu.xinqingpojo.DTO.ActiveAnalyseDTO;
import dev.langchain4j.model.output.structured.Description;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class TempEmotionRecordsVO {
    @Description("学生信息")
    private UserVO student;

    @Description("检测时间")
    private Date detectionTime;

    @Description("情感类型:快乐、愤怒、悲伤、恐惧、厌恶、惊讶、好")
    private String emotionType;

    @Description("情感置信度0.0~1.0")
    private BigDecimal confidence;

    @Description("分析出情感类型最具代表的博客")
    private BlogVO featureBlog;

    @Description("分析出情感类型最具代表的评论")
    private ActiveAnalyseDTO featureActive;

    @Description("分析出情感类型最具代表的评论内容或者与AI聊天内容")
    private String relatedContent;

    @Description("是否异常，0正常，1异常")
    private Integer isAbnormal;
}
