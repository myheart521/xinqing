package com.hpu.xinqingpojo.DTO;

import dev.langchain4j.model.output.structured.Description;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Description("与AI聊天的内容")
public class ChatWithAIAnalyseDTO {

    @Description("与AI聊天的内容列表")
    private String content;

    @Description("时间")
    private LocalDateTime createTime;

}
