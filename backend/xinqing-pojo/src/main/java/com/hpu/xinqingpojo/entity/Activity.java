package com.hpu.xinqingpojo.entity;

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
 * 活动
 * </p>
 *
 * @since 2025-02-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("activity")
public class Activity implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 标语,标题
     */
    private String title;

    /**
     * 详细内容
     */
    private String content;

    /**
     * 图片,多张用","隔开
     */
    private String images;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 时间
     */
    private LocalDateTime startTime;

    /**
     * 标签,主题,多个用","隔开
     */
    private String tag;

    /**
     * 地点
     */
    private String address;

    /**
     * 经度
     */
    private Double longitude;

    /**
     * 维度
     */
    private Double latitude;

    /**
     * 关注参与的数量
     */
    private Integer follow;

    /**
     * 发起人
     */
    private Long userId;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;


    /**
     * 浏览量
     */
    private Long viewUserCount;

    /**
     * 评论数量
     */
    private Integer commentCount;


    /**
     * 点赞数量
     */
    private Integer likeCount;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    /**
     * 标签颜色
     */
    private String color;



}
