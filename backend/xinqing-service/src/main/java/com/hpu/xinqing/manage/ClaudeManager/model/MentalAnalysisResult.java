package com.hpu.xinqing.manage.ClaudeManager.model;

import com.hpu.xinqingpojo.DTO.NewKnowledgeAnalyzeDTO;
import lombok.Data;

import java.util.List;

@Data
public class MentalAnalysisResult {
    // 根据AI反馈的心理健康评价文本 (例如："用户表现出轻度抑郁倾向")
    private String summary;

    // 心理健康指标评分体系 (0-10分，数值越高越健康)
    private MentalHealthMetrics metrics;
    //关键字
    private List<String> keywords;
    // 前端展示建议
    private DisplayRecommendation visualization;

    //建议观看的文章
    private NewKnowledgeAnalyzeDTO recommendArticles;

}
