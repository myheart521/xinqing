package com.hpu.xinqingpojo.DTO;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 活动
 * </p>
 *
 * @since 2025-02-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class ActivityDTO implements Serializable {


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
    private List<String> imageList;


    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    /**
     * 标签,主题,多个用","隔开
     */
    private String label;

    /**
     * 标签颜色
     */
    private String color;

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



}
