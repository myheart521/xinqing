package com.hpu.xinqingpojo.DTO;

import dev.langchain4j.model.output.structured.Description;

import java.time.LocalDateTime;
import java.util.List;

@Description("学生发布的评论")
public class CommentAnalyseDTO {
    @Description("评论id")
    private Long id;

    @Description("评论内容")
    private String content;

    @Description("评论时间")
    private LocalDateTime createTime;
}
