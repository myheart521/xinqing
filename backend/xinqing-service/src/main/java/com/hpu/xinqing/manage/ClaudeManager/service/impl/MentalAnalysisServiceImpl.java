package com.hpu.xinqing.manage.ClaudeManager.service.impl;

import cn.hutool.core.bean.BeanUtil;
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
import com.hpu.xinqing.utils.common.JSONUtil;
import com.hpu.xinqingcommon.exception.AnalysisException;
import com.hpu.xinqingpojo.DTO.ActiveAnalyseDTO;
import com.hpu.xinqingpojo.DTO.BlogAnalyseDTO;
import com.hpu.xinqingpojo.DTO.NewKnowledgeAnalyzeDTO;
import com.hpu.xinqingpojo.entity.NewKnowledge;
import dev.langchain4j.community.model.dashscope.QwenChatModel;
import dev.langchain4j.community.model.dashscope.QwenLanguageModel;
import dev.langchain4j.model.output.Response;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.io.DataInput;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class MentalAnalysisServiceImpl implements MentalAnalysisService {
    //    private final ChatClient chatClient; // 假设已注入AI客户端
    @Resource
    private UserService userService;

    public static void main(String[] args) {
        List<Integer> integers = List.of(1, 2, 3);
        integers.add(4);
        System.out.println("integers = " + integers);
    }
    private final ObjectMapper objectMapper; // 由Spring自动注入

    @Resource
    private JSONUtil jsonUtil;

    @Resource
    private NewKnowledgeService newKnowledgeService;

    private static final Pattern JSON_PATTERN = Pattern.compile(
            "(?s)```(?:json\\s*)?([\\s\\S]*?)```",
            Pattern.DOTALL
    );


    public MentalAnalysisResult analyzeUserBehavior(Long userId) throws IOException {
        QwenLanguageModel model = QwenLanguageModel.builder()
                .apiKey(System.getenv("DASHSCOPE_API_KEY"))
                .modelName("qwen-max")
                .enableSearch(true)
                .build();
        // 1.获取原始数据
        List<BlogAnalyseDTO> blogs = userService.getBlogByUserId(userId);
        List<ActiveAnalyseDTO> activities = userService.getActivityByUserId(userId);
        List<NewKnowledgeAnalyzeDTO> knowledgeArticles = getKnowledgeArticles();

        // 2.生成AI指令
        String prompt = ClaudePromptBuilder.buildAnalysisPrompt(blogs, activities, knowledgeArticles);
        System.out.println("prompt:" + prompt);

        // 3.调用AI分析
        Response<String> response = model.generate(prompt);
        String rawAnalysis = response.content();
        System.out.println("rawAnalysis:" + rawAnalysis);
        Matcher matcher = null;
        String matchResult = null;
        if (rawAnalysis != null) {
            matcher = JSON_PATTERN.matcher(rawAnalysis);
        }
        if (matcher != null && matcher.find() && matcher.groupCount() >= 1) {
            matchResult = matcher.group(1);
        }
        System.out.println("matchResult:" + matchResult);
        ObjectMapper mapper = new ObjectMapper();
        MentalAnalysisResult report = mapper.readValue(matchResult, MentalAnalysisResult.class);
        System.out.println("report:" + report);
        // 4.解析AI响应
        return report;
    }

    private MentalAnalysisResult parseAnalysisResult(String rawJson) {
        try {
            JsonNode root = objectMapper.readTree(rawJson);

            MentalHealthMetrics metrics = new MentalHealthMetrics();
            metrics.setEmotionalStability(root.path("metrics").path("emotional_stability").asDouble());
            metrics.setSocialEngagement(root.path("metrics").path("social_engagement").asDouble());
            metrics.setStressLevel(root.path("metrics").path("stress_level").asDouble());

            DisplayRecommendation display = new DisplayRecommendation();
            display.setCharts(mapChartTypes(root.path("visualization").path("charts")));
//            display.setHighlightKeywords(mapKeywords(root.path("keywords")));
//            display.setInteractionSuggestion("点击图表查看详细解读");
//
            MentalAnalysisResult result = new MentalAnalysisResult();
//            result.setAnalysisSummary(root.path("summary").asText());
//            result.setMetrics(metrics);
//            result.setDisplayRecommendation(display);
//            result.setRawAnalysisData(rawJson);

            return result;
        } catch (Exception e) {
            throw new AnalysisException("AI结果解析失败:" + e);
        }
    }

    private List<ChartType> mapChartTypes(JsonNode node) {
        return StreamSupport.stream(node.spliterator(), false)
                .map(n -> ChartType.valueOf(n.asText().toUpperCase()))
                .collect(Collectors.toList());
    }

    private List<String> mapKeywords(JsonNode node) {
        return StreamSupport.stream(node.spliterator(), false)
                .map(JsonNode::asText)
                .collect(Collectors.toList());
    }


    /**
     * 获取知识科普文章，并转换为适合的格式
     */
    private List<NewKnowledgeAnalyzeDTO> getKnowledgeArticles() {
        List<NewKnowledge> all = newKnowledgeService.getAll();
        //转换为List<NewKnowledgeAnalyzeDTO>
        return all.stream().map(item -> {
            NewKnowledgeAnalyzeDTO dto = new NewKnowledgeAnalyzeDTO();
            dto.setTitle(item.getTitle());
            dto.setId(item.getId());
            dto.setDescriptions(item.getDescriptions());
            return dto;
        }).collect(Collectors.toList());
    }
}
