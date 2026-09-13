package com.hpu.xinqingpojo.VO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NewKnowledgeListVO {
    private static final long serialVersionUID = 1L;

    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * 描述
     */
    private String desc;

    /**
     * 外部图片
     */
    private String coverImage;

    /**
     * 标签
     */
    private String[] tags;

    /**
     * 标签颜色
     */
    private String color;

    private Integer viewCount;

    private Integer likeCount;

    private Integer collectionCount;

    private Boolean isLiked;
    private Boolean isCollection;
}
