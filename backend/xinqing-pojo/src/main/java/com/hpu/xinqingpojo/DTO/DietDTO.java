package com.hpu.xinqingpojo.DTO;

import lombok.Data;

import java.io.Serializable;

@Data
public class DietDTO implements Serializable {
    private static final long serialVersionUID = 1L;

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

    public boolean isMainNotNull(){
        return title != null
                && name != null
                && mainImage != null
                && content != null
                && tags != null;
    }

}
