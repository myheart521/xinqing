package com.hpu.xinqingpojo.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 助眠
 * </p>
 *
 * @since 2025-03-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("sleep_audios")
public class SleepAudios implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String name;

    /**
     * 图片
     */
    private String cover;

    /**
     * 音乐
     */
    private String url;

    /**
     * 持续时间
     */
    private String duration;

    /**
     * 描述
     */
    private String description;

    /**
     * 聆听人数
     */
    private Integer plays;

    /**
     * 类型id
     */
    private Integer typeId;


}
