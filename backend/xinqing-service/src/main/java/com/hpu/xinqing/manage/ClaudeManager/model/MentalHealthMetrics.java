package com.hpu.xinqing.manage.ClaudeManager.model;

import lombok.Data;

@Data
public class MentalHealthMetrics {
    // 情绪稳定性评分 example = "7.2"
    private Double emotionalStability;
    // 心理健康评分 example = "8.1"
    private Double socialEngagement;
    // 压力水平评分 example = "6.5"
    private Double stressLevel;
}
