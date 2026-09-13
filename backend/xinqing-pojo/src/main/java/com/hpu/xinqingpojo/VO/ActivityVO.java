package com.hpu.xinqingpojo.VO;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
public class ActivityVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 发起人
     */
    private Long userId;

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 用户头像
     */
    private String userAvatar;


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
    private List<String> mainImage;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;
    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    /**
     * 标签,一个
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

    /**
     * 关注参与的数量
     */
    private Integer follow;


    //参与用户的头像,与id
    private List<UserDTO> groupList;

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
     * 更新时间
     */
    private LocalDateTime updateTime;




}
