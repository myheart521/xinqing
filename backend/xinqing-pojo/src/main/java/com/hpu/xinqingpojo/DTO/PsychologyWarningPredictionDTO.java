package com.hpu.xinqingpojo.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PsychologyWarningPredictionDTO {

    @JsonProperty("userId")
    private Long userId;

    @JsonProperty("emotion_label")
    private String emotionLabel;

    @JsonProperty("emotion_confidence")
    private BigDecimal emotionConfidence;

    @JsonProperty("risk_level")
    private Integer riskLevel;

    @JsonProperty("risk_confidence")
    private BigDecimal riskConfidence;

    @JsonProperty("risk_probs")
    private Map<String, BigDecimal> riskProbs;
}
