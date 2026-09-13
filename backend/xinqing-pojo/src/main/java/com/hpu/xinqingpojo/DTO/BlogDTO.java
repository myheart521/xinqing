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
 * 帖子
 * </p>
 *
 * @since 2025-02-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class BlogDTO implements Serializable {

    /**
     * 标题
     */
    private String title;

    /**
     * 标签,多个标签用","隔开
     */
    private List<String> tags;

    /**
     * 图片，最多6张，多张以","隔开
     */
    private List<String> images;

    /**
     * 详细内容
     */
    private String content;

    /**
     * 对应圈子
     */
    private Integer circleId;

}
