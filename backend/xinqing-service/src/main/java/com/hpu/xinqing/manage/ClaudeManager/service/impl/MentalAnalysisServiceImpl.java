package com.hpu.xinqing.manage.ClaudeManager.service.impl;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hpu.xinqing.manage.ClaudeManager.enums.ChartType;
import com.hpu.xinqing.manage.ClaudeManager.model.ClaudePromptBuilder;
import com.hpu.xinqing.manage.ClaudeManager.model.DisplayRecommendation;
import com.hpu.xinqing.manage.ClaudeManager.model.MentalAnalysisResult;
import com.hpu.xinqing.manage.ClaudeManager.model.MentalHealthMetrics;
import com.hpu.xinqing.manage.ClaudeManager.service.MentalAnalysisService;
import com.hpu.xinqing.service.NewKnowledgeService;
import com.hpu.xinqing.service.UserService;
import com.hpu.xinqingcommon.exception.AnalysisException;
import com.hpu.xinqingpojo.DTO.ActiveAnalyseDTO;
import com.hpu.xinqingpojo.DTO.BlogAnalyseDTO;
import com.hpu.xinqingpojo.DTO.NewKnowledgeAnalyzeDTO;
import com.hpu.xinqingpojo.entity.NewKnowledge;
import dev.langchain4j.model.openai.OpenAiLanguageModel;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class MentalAnalysisServiceImpl implements MentalAnalysisService {

    @Resource
    private UserService userService;

    @Resource(name = "mentalWarningLanguageModel")
    private OpenAiLanguageModel mentalWarningLanguageModel;

    private final ObjectMapper objectMapper;

    @Resource
    private NewKnowledgeService newKnowledgeService;

    private static final Pattern JSON_PATTERN = Pattern.compile("(?s)```(?:json\\s*)?([\\s\\S]*?)```");

    @Override
    public MentalAnalysisResult analyzeUserBehavior(Long userId) throws IOException {
        List<BlogAnalyseDTO> blogs = userService.getBlogByUserId(userId);
        List<ActiveAnalyseDTO> activities = userService.getActivityByUserId(userId);
        List<NewKnowledgeAnalyzeDTO> knowledgeArticles = getKnowledgeArticles();

        String prompt = ClaudePromptBuilder.buildAnalysisPrompt(blogs, activities, knowledgeArticles);
        System.out.println("prompt:" + prompt);

        String rawAnalysis = mentalWarningLanguageModel.generate(prompt).content();
        System.out.println("rawAnalysis:" + rawAnalysis);

        String jsonPayload = extractJsonPayload(rawAnalysis);
        System.out.println("jsonPayload:" + jsonPayload);

        MentalAnalysisResult report = parseAnalysisResult(jsonPayload);
        System.out.println("report:" + report);
        return report;
    }

    static String extractJsonPayload(String rawAnalysis) {
        if (rawAnalysis == null || rawAnalysis.isBlank()) {
            throw new AnalysisException("AI结果为空");
        }

        String trimmed = rawAnalysis.trim();
        Matcher matcher = JSON_PATTERN.matcher(trimmed);
        if (matcher.find() && matcher.groupCount() >= 1) {
            return matcher.group(1).trim();
        }

        int firstBrace = trimmed.indexOf('{');
        int lastBrace = trimmed.lastIndexOf('}');
        if (firstBrace >= 0 && lastBrace > firstBrace) {
            return trimmed.substring(firstBrace, lastBrace + 1).trim();
        }

        return trimmed;
    }

    private MentalAnalysisResult parseAnalysisResult(String rawJson) {
        try {
            ObjectMapper lenientMapper = objectMapper.copy()
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            return lenientMapper.readValue(rawJson, MentalAnalysisResult.class);
        } catch (Exception ignored) {
            try {
                JsonNode root = objectMapper.readTree(rawJson);
                return buildAnalysisResultFromTree(root);
            } catch (Exception e) {
                throw new AnalysisException("AI结果解析失败:" + e.getMessage());
            }
        }
    }

    private MentalAnalysisResult buildAnalysisResultFromTree(JsonNode root) {
        MentalAnalysisResult result = new MentalAnalysisResult();
        result.setSummary(readText(root, "summary", "analysisSummary"));

        JsonNode metricsNode = root.path("metrics");
        MentalHealthMetrics metrics = new MentalHealthMetrics();
        metrics.setEmotionalStability(readDouble(metricsNode, "emotionalStability", "emotional_stability"));
        metrics.setSocialEngagement(readDouble(metricsNode, "socialEngagement", "social_engagement"));
        metrics.setStressLevel(readDouble(metricsNode, "stressLevel", "stress_level"));
        result.setMetrics(metrics);

        result.setKeywords(readStringList(root.path("keywords")));

        JsonNode visualizationNode = root.path("visualization");
        DisplayRecommendation display = new DisplayRecommendation();
        display.setCharts(mapChartTypes(visualizationNode.path("charts")));
        display.setReason(readText(visualizationNode, "reason"));
        result.setVisualization(display);

        JsonNode recommendNode = root.path("recommendArticles");
        if (!recommendNode.isMissingNode() && !recommendNode.isNull()) {
            try {
                result.setRecommendArticles(objectMapper.treeToValue(recommendNode, NewKnowledgeAnalyzeDTO.class));
            } catch (Exception e) {
                throw new AnalysisException("AI推荐文章解析失败:" + e.getMessage());
            }
        }

        return result;
    }

    private List<ChartType> mapChartTypes(JsonNode node) {
        if (node == null || node.isNull() || !node.isArray()) {
            return new ArrayList<>();
        }
        return StreamSupport.stream(node.spliterator(), false)
                .map(n -> ChartType.valueOf(n.asText().toUpperCase()))
                .collect(Collectors.toList());
    }

    private List<String> readStringList(JsonNode node) {
        if (node == null || node.isNull() || !node.isArray()) {
            return new ArrayList<>();
        }
        return StreamSupport.stream(node.spliterator(), false)
                .map(JsonNode::asText)
                .collect(Collectors.toList());
    }

    private String readText(JsonNode node, String... fieldNames) {
        for (String fieldName : fieldNames) {
            JsonNode field = node.path(fieldName);
            if (!field.isMissingNode() && !field.isNull()) {
                String text = field.asText();
                if (!text.isBlank()) {
                    return text;
                }
            }
        }
        return null;
    }

    private double readDouble(JsonNode node, String... fieldNames) {
        for (String fieldName : fieldNames) {
            JsonNode field = node.path(fieldName);
            if (field.isMissingNode() || field.isNull()) {
                continue;
            }
            if (field.isNumber()) {
                return field.asDouble();
            }
            try {
                return Double.parseDouble(field.asText());
            } catch (NumberFormatException ignored) {
                // try next candidate field
            }
        }
        return 0D;
    }

    /**
     * 获取知识科普文章，并转换为适合的格式
     */
    private List<NewKnowledgeAnalyzeDTO> getKnowledgeArticles() {
        List<NewKnowledge> all = newKnowledgeService.getAll();
        return all.stream().map(item -> {
            NewKnowledgeAnalyzeDTO dto = new NewKnowledgeAnalyzeDTO();
            dto.setTitle(item.getTitle());
            dto.setId(item.getId());
            dto.setDescriptions(item.getDescriptions());
            return dto;
        }).collect(Collectors.toList());
    }
}
