package com.hpu.xinqingpojo.VO;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class DietDetailVo {

    /**
     * 健康文章标题
     */
    private String title;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 文章具体内容
     */
    private String content;


}
