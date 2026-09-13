package com.hpu.xinqingpojo.VO;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class NewKnowledgeInfoVo {

    /**
     * 标题
     */
    private String title;

    /**
     * 创建时间
     */
    private LocalDate creatTime;

    /**
     * 文章具体内容
     */
    private String content;

}
