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
 *轮播图, 作为引用, 指向sport表和diet表
 * @since 2025-02-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("swiper")
public class Swiper implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    //图片url
    private String url;
    //标题
    private String name;
    //小简介
    private String text;

    //命名错误, 相当于type
    private String link;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    /**
     * -1代表匿名
     */
    private Long createId;


}
