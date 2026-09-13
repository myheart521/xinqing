package com.hpu.xinqingpojo.VO;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.time.LocalDateTime;
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class HotArticleVO {
    private Long id;

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
     * 标签颜色
     */
    private String color;

    /**
     * 标签
     */
    private String tags;
    /**
     * 浏览量
     */
    private Integer viewCount;

    /**
     * 点赞量
     */
    private Integer likeCount;

    /**
     * 收藏量
     */
    private Integer collectionCount;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    private String userName;

}
