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
 *
 * </p>
 *
 * @since 2025-02-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("diet")
public class Diet implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 健康文章标题
     */
    private String title;

    /**
     * 主要菜品名称
     */
    private String name;

    /**
     * 外部展示图片
     */
    private String mainImage;

    /**
     * 文章具体内容
     */
    private String content;

    /**
     * 文章标签
     */
    private String tags;

    /**
     * 文章创建人
     */
    private Long userId;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;


}
