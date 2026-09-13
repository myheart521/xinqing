package com.hpu.xinqingpojo.VO;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DietVO {

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
     * 文章标签
     */
    private String[] tags;

}
