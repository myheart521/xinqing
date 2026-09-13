package com.hpu.xinqing.TimeTask.warning.tempwarning;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hpu.xinqingpojo.DTO.ActiveAnalyseDTO;
import com.hpu.xinqingpojo.DTO.BlogAnalyseDTO;
import com.hpu.xinqingpojo.DTO.ChatWithAIAnalyseDTO;
import com.hpu.xinqingpojo.DTO.CommentAnalyseDTO;
import com.hpu.xinqingpojo.DTO.PsychologyWarningPredictionDTO;
import com.hpu.xinqingpojo.DTO.TempEmotionUserDataDTO;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MentalWarningPolicyTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final MentalWarningPolicy policy = new MentalWarningPolicy();

    @Test
    void shouldParseFineTunedEmotionPayload() throws Exception {
        String json = """
                {
                  "emotion_label": "焦虑",
                  "emotion_confidence": 0.990757942199707,
                  "risk_level": 1,
                  "risk_confidence": 0.6670640707015991,
                  "risk_probs": {
                    "0": 0.04336080327630043,
                    "1": 0.6670640707015991,
                    "2": 0.25966688990592957,
                    "3": 0.01944022811949253,
                    "4": 0.010468050837516785
                  }
                }
                """;

        PsychologyWarningPredictionDTO dto = objectMapper.readValue(json, PsychologyWarningPredictionDTO.class);

        assertEquals("焦虑", dto.getEmotionLabel());
        assertEquals(1, dto.getRiskLevel());
        assertEquals(5, dto.getRiskProbs().size());
        assertTrue(dto.getRiskConfidence().compareTo(BigDecimal.valueOf(0.6)) > 0);
    }

    @Test
    void shouldMapRiskLevelToReasonableAlertLevel() {
        assertEquals(3, policy.mapAlertLevel(prediction(0, 0.95)));
        assertEquals(0, policy.mapAlertLevel(prediction(1, 0.70)));
        assertEquals(3, policy.mapAlertLevel(prediction(1, 0.40)));
        assertEquals(1, policy.mapAlertLevel(prediction(2, 0.70)));
        assertEquals(0, policy.mapAlertLevel(prediction(2, 0.40)));
        assertEquals(2, policy.mapAlertLevel(prediction(3, 0.80)));
        assertEquals(2, policy.mapAlertLevel(prediction(4, 0.55)));
    }

    @Test
    void shouldBuildEvidenceFromRecentContent() {
        TempEmotionUserDataDTO source = new TempEmotionUserDataDTO();
        source.setId(7L);

        BlogAnalyseDTO blog = new BlogAnalyseDTO();
        blog.setId(11L);
        blog.setTitle("期末压力");
        blog.setContent("最近总是很焦虑，晚上睡不好");
        source.setBlogAnalyseList(List.of(blog));

        ActiveAnalyseDTO active = new ActiveAnalyseDTO();
        active.setId(22L);
        active.setTitle("心理讲座");
        active.setContent("我还是有点回避交流");
        source.setActiveAnalyseList(List.of(active));

        CommentAnalyseDTO comment = new CommentAnalyseDTO();
        comment.setContent("我真的很无助");
        source.setNoteAnalyseList(List.of(comment));

        ChatWithAIAnalyseDTO chat = new ChatWithAIAnalyseDTO();
        chat.setContent("最近确实压力很大");
        source.setChatWithAIAnalyseList(List.of(chat));

        PsychologyWarningPredictionDTO prediction = prediction(1, 0.72);
        prediction.setEmotionLabel("焦虑");

        assertEquals(11L, policy.selectFeatureBlogId(source));
        assertEquals(22L, policy.selectFeatureActiveId(source));
        assertTrue(policy.buildRelatedContent(source).contains("期末压力"));
        assertTrue(policy.buildRecentBehavior(prediction, source).contains("焦虑"));
        assertTrue(policy.buildSuggestion(prediction, 0).contains("关注"));
    }

    private PsychologyWarningPredictionDTO prediction(int riskLevel, double confidence) {
        PsychologyWarningPredictionDTO dto = new PsychologyWarningPredictionDTO();
        dto.setUserId(1L);
        dto.setEmotionLabel("焦虑");
        dto.setEmotionConfidence(BigDecimal.valueOf(0.9));
        dto.setRiskLevel(riskLevel);
        dto.setRiskConfidence(BigDecimal.valueOf(confidence));
        return dto;
    }
}
