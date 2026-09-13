package com.hpu.xinqingpojo.DTO;

import dev.langchain4j.model.output.structured.Description;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Description("student comment analysis")
public class CommentAnalyseDTO {
    @Description("comment id")
    private Long id;

    @Description("comment content")
    private String content;

    @Description("comment create time")
    private LocalDateTime createTime;
}
