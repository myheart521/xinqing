package com.hpu.xinqingpojo.VO;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.hpu.xinqingpojo.DTO.UserDTO;
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
public class ActivityAllVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 发起人
     */
    private Long userId;


    /**
     * 标语,标题
     */
    private String title;


    /**
     * 图片,多张用","隔开
     */
    private List<String> mainImage;

    private String images;


    /**
     * 标签,一个
     */
    private String tag;

    /**
     * 标签颜色
     */
    private String color;


    /**
     * 关注参与的数量
     */
    private Integer follow;


    /**
     * 浏览量
     */
    private Long viewUserCount;

    /**
     * 热度量
     */
    private Long heatCount;









}
