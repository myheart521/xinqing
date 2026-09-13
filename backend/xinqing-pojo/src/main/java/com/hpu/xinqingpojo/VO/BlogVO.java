package com.hpu.xinqingpojo.VO;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hpu.xinqingpojo.entity.Comments;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 帖子
 * </p>
 *
 * @since 2025-02-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class BlogVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 动态用户id
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
     * 用户头像
     */
    private String userProfile;

    /**
     * 标题
     */
    private String title;

    /**
     * 标签,多个标签用","隔开
     */
    private List<String> label;

    /**
     * 图片，最多6张，多张以","隔开
     */
    private List<String> mainImage;

    /**
     * 详细内容
     */
    private String content;

    /**
     * 点赞数
     */
    private Integer liked;

    //评论数
    private Integer comments;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 对应圈子
     */
    private Integer circleId;

    /**
     * 收藏数
     */
    private Integer collectionCount;

    /**
     * 浏览量
     */
    private Long viewUserCount;


    //是否点赞
    private Boolean isLike;

    //动态下的一级评论列表
    private List<CommentVO> commentData;

    //点赞用户前10名头像
    private List<String> likedUser;

    //距离
    private Double distance;




}
