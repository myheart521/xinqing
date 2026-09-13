package com.hpu.xinqing.manage.ClaudeManager.service;

import com.hpu.xinqing.manage.ClaudeManager.model.MentalAnalysisResult;

import java.io.IOException;

public interface MentalAnalysisService {

    public MentalAnalysisResult analyzeUserBehavior(Long userId) throws IOException;
}
