package com.hpu.xinqingpojo.VO;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 评论表,包括多级评论
 * </p>
 *
 * @since 2025-02-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class CommentVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户id
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
     * 回复用户昵称
     */
    private String answerUserName;


    /**
     * 帖子id
     */
    private Long blogId;

    /**
     * 关联的1级评论id,如果为1级评论为0
     */
    private Long parentId;


    /**
     * 回复的评论Id,如果为一级评论为0
     */
    private Long answerId;

    /**
     * 回复的评论内容
     */
    private String answerText;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 点赞数
     */
    private Integer liked;

    /**
     * 评论数
     *只有一级评论存在
     */
    private Integer commentCount;
    /**
     * 状态，0：正常，1：被举报，2：禁止查看
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
    /**
     * 标题
     */
    private String title;

    /**
     * 是否点赞
     */
    private Boolean likeActive;


}
