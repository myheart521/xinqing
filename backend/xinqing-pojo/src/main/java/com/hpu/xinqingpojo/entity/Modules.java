package com.hpu.xinqingpojo.entity;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.apache.ibatis.annotations.Delete;

/**
 * <p>
 *
 * </p>
 *
 * @since 2025-03-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("modules")
public class Modules implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String title;

    private String introductions;

    private String src;

    private Integer status;

    private Long userId;

    private String resultDescription;

    private String resultSrc;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
    @TableLogic
    private Boolean isDeleted;
    /**
     * 是否一个题目可以测出来被测者的多个特征
     */
    private Boolean isMultiple;

}
