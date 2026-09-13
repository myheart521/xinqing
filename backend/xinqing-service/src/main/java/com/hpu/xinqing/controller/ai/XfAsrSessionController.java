package com.hpu.xinqing.controller.ai;

import cn.dev33.satoken.stp.StpUtil;
import com.hpu.xinqing.service.ai.XfAsrSigner;
import com.hpu.xinqingcommon.result.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;

@RestController
public class XfAsrSessionController {
    @Value("${xinqing.asr.enabled:false}")
    private boolean enabled;
    @Value("${XINQING_ASR_APP_ID:}")
    private String appId;
    @Value("${XINQING_ASR_API_KEY:}")
    private String apiKey;
    @Value("${XINQING_ASR_API_SECRET:}")
    private String apiSecret;

    @PostMapping("/ai/asr/session")
    public Result<XfAsrSigner.Session> createSession() {
        StpUtil.checkLogin();
        if (!enabled) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Speech recognition is not enabled");
        }
        try {
            return Result.success(XfAsrSigner.sign(appId, apiKey, apiSecret, Instant.now()));
        } catch (IllegalArgumentException error) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Speech recognition is not configured");
        }
    }
}
