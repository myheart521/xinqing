package com.hpu.xinqingpojo.VO;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hpu.xinqingpojo.entity.Blog;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 圈子
 * </p>
 *
 * @since 2025-02-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class CircleVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 标题
     */
    private String name;

    /**
     * 图片,头像,背景图
     */
    private String url;

    /**
     * 简介
     */
    private String content;

    /**
     * 关注的数量
     */
    private Integer follow;

    @TableField(exist = false)
    private String text;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 圈子中的动态信息
     */
    private List<BlogVO> blogList;



}
