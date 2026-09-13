package com.hpu.xinqing.manage.ClaudeManager.model;

import com.hpu.xinqing.manage.ClaudeManager.enums.ChartType;
import lombok.Data;

import java.util.List;

@Data
public class DisplayRecommendation {
    // 建议使用的图表类型集合
    private List<ChartType> charts;
    // 交互建议示例
    private String reason;
}
