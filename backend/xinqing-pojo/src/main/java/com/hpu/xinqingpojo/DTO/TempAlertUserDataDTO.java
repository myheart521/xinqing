package com.hpu.xinqingpojo.DTO;

import com.hpu.xinqingpojo.entity.Activity;
import com.hpu.xinqingpojo.entity.Blog;
import dev.langchain4j.model.output.structured.Description;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TempAlertUserDataDTO {

    @Description("关联的情感记录ID")
    private Long emotionRecordId;

    @Description("学生id")
    private Long userId;

    @Description("情感类型:快乐、愤怒、悲伤、恐惧、厌恶、惊讶、好")
    private String emotionType;

    @Description("情感置信度0.0~1.0")
    private BigDecimal confidence;

    @Description("分析出情感类型最具代表的博客")
    private Blog featureBlog;

    @Description("分析出情感类型最具代表的评论")
    private Activity featureActive;

    @Description("分析出情感类型最具代表的评论内容或者与AI聊天内容")
    private String relatedContent;

    @Description("是否异常，0正常，1异常")
    private Integer isAbnormal;
}
