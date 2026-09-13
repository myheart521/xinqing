package com.hpu.xinqingpojo.DTO;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class NewKnowledgeDTO implements Serializable {


    private static final long serialVersionUID = 1L;


    /**
     * 标题
     */
    private String title;

    /**
     * 描述
     */
    private String descriptions;

    /**
     * 外部图片
     */
    private String coverImage;

    /**
     * 标签
     */
    private String tags;

    /**
     * 标签颜色
     */
    private String color;

    /**
     * 文章具体内容
     */
    private String content;

    /**
     * 创建人id，-1代表游客等未插入用户id的情况
     */
    private Long createId;

    public boolean isMainNotNull() {
        return title != null &&
                descriptions != null &&
                coverImage != null &&
                tags != null &&
                color != null &&
                content != null;
    }
}
