package com.hpu.xinqingpojo.DTO;

import dev.langchain4j.model.output.structured.Description;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@Description("情感分析使用的用户数据")
@Setter
@Getter
public class TempEmotionUserDataDTO {
    @Description("学生ID")
    private Long id;

    @Description("学生参与的活动")
    private List<ActiveAnalyseDTO> activeAnalyseList;

    @Description("学生发布的动态")
    private List<BlogAnalyseDTO> blogAnalyseList;

    @Description("与AI聊天的内容")
    private List<ChatWithAIAnalyseDTO> chatWithAIAnalyseList;

    @Description("学生发布的评论")
    private List<CommentAnalyseDTO> noteAnalyseList;
}
