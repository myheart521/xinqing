package com.hpu.xinqing.manage.ClaudeManager.model;

import com.hpu.xinqingpojo.DTO.ActiveAnalyseDTO;
import com.hpu.xinqingpojo.DTO.BlogAnalyseDTO;
import com.hpu.xinqingpojo.DTO.NewKnowledgeAnalyzeDTO;

import java.util.List;
import java.util.stream.Collectors;

public class ClaudePromptBuilder {
    private static final String SYSTEM_PROMPT_TEMPLATE = """
            你是一位专业的心理健康分析师，需要根据以下用户生成内容进行心理评估：
                    
            ### 用户博客摘要（最多3条）
            %s
                    
            ### 用户活动记录（最多3条） 
            %s
                 
            ### 文章标题和id和文章描述（所有的文章） 
            %s
               
            请按照以下结构化要求分析：
            1. 情绪状态评估（抑郁、焦虑等倾向）
            2. 社交活跃程度的量化分析
            3. 压力源识别与压力等级评估
            4. 生成3个心理状态关键词
            5. 建议的可视化方案及理由,只能在下面四种选取:RADAR_CHART,WORD_CLOUD,LINE_CHART,BAR_CHART    
            
            输出格式使用以下严格JSON结构：
            {
                "summary": "综合分析结论...",
                "metrics": {
                    "emotionalStability": 0-10,
                    "socialEngagement": 0-10, 
                    "stressLevel": 0-10
                },
                "keywords": ["关键词1", "关键词2", "关键词3"],
                "visualization": {
                    "charts": ["RADAR_CHART", "LINE_CHART"],
                    "reason": "雷达图适合展示多维度评分对比..."
                },
                "recommendArticles": {
                    "id": 1,
                    "title": "文章标题",
                    "descriptions": "文章描述"
                }
            }
            """;

    public static String buildAnalysisPrompt(List<BlogAnalyseDTO> blogs,
                                             List<ActiveAnalyseDTO> activities, List<NewKnowledgeAnalyzeDTO> knowledgeAnalyzeDTOS) {
        String blogStr = blogs.stream()
                .map(b -> String.format("""
                                - 标题：《%s》
                                  内容摘要：%s
                                  发布圈子：%s
                                  标签：%s
                                """, b.getTitle(),
                        abbreviate(b.getContent(), 100),
                        b.getCircleName(),
                        String.join(",", b.getLabel())))
                .collect(Collectors.joining("\n"));

        String activityStr = activities.stream()
                .map(a -> String.format("""
                                - 活动：《%s》
                                  地点：%s
                                  标签：%s
                                  内容：%s
                                """, a.getTitle(), a.getAddress(),
                        a.getTag(), abbreviate(a.getContent(), 100)))
                .collect(Collectors.joining("\n"));
        String knowledgeStr = knowledgeAnalyzeDTOS.stream()
                .map(a -> String.format("""
                                - 标题：《%s》
                                  文章id：%s
                                  描述：%s
                                """, a.getTitle(), a.getId(), abbreviate(a.getDescriptions(), 100)))
                .collect(Collectors.joining("\n"));
        return String.format(SYSTEM_PROMPT_TEMPLATE, blogStr, activityStr, knowledgeStr);
    }

    private static String abbreviate(String text, int maxLength) {
        return text.length() > maxLength ? text.substring(0, maxLength) + "..." : text;
    }
}
