package com.hpu.xinqingpojo.DTO;

import dev.langchain4j.model.output.structured.Description;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Description("博客分析")
public class BlogAnalyseDTO {
    //博客id
    @Description("博客id")
    private Long id;

    //博客标题
    @Description("博客标题")
    private String title;

    //博客标签
    @Description("博客标签")
    private List<String> label;

    //博客内容
    @Description("博客内容")
    private String content;

    //博客发布的圈子名字
    @Description("博客发布的圈子名字")
    private String circleName;

    @Description("博客发布的时间")
    private LocalDateTime createTime;
}
