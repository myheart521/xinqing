package com.hpu.xinqingpojo.entity;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @since 2025-07-06
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("temp_emotion_records")
public class TempEmotionRecords implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 关联用户ID
     */
    private Long userId;

    /**
     * 检测时间
     */
    private LocalDateTime detectionTime;

    /**
     * 情感类型
     */
    private String emotionType;

    /**
     * 识别置信度（0.0~1.0）
     */
    private BigDecimal confidence;

    /**
     * 关联最具代表的博客ID
     */
    private Long featureBlogId;

    /**
     * 关联最具代表的活动
     */
    private Long featureActiveId;

    /**
     * 关联内容（与AI对话文本、发布评论的文本）
     */
    private String relatedContent;

    /**
     * 是否异常标记
     */
    private Boolean isAbnormal;


}
