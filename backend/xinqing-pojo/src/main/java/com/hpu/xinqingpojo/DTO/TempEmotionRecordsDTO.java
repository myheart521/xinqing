package com.hpu.xinqingpojo.DTO;

import com.baomidou.mybatisplus.annotation.TableName;
import dev.langchain4j.model.output.structured.Description;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * @TableName temp_emotion_records
 */
@TableName(value ="temp_emotion_records")
@Data
@Description("情感记录")
public class TempEmotionRecordsDTO {

    @Description("学生id")
    private Integer userId;

    @Description("情感类型:快乐、愤怒、悲伤、恐惧、厌恶、惊讶、好")
    private String emotionType;

    @Description("情感置信度0.0~1.0")
    private BigDecimal confidence;

    @Description("分析出情感类型最具代表的博客id")
    private Long featureBlogId;

    @Description("分析出情感类型最具代表的评论id")
    private Long featureActiveId;

    @Description("分析出情感类型最具代表的评论内容或者与AI聊天内容")
    private String relatedContent;

    @Description("是否异常，0正常，1异常")
    private Integer isAbnormal;

    // 增加转换方法保持兼容性
}
