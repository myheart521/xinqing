package com.hpu.xinqing.controller.ai;


import com.hpu.xinqing.service.ai.IAiApiKeyService;
import com.hpu.xinqingcommon.result.PageResult;
import com.hpu.xinqingcommon.result.Result;
import com.hpu.xinqingpojo.VO.AiApiKeyPageReqVO;
import com.hpu.xinqingpojo.VO.AiApiKeySaveReqVO;
import com.hpu.xinqingpojo.entity.AiApiKey;
import io.swagger.v3.oas.annotations.Operation;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "管理后台 - AI API 密钥")
@RestController
@RequestMapping("/ai/api-key")
@Validated
public class AiApiKeyController {

    @Resource
    private IAiApiKeyService apiKeyService;

    @PostMapping("/create")
    @Operation(summary = "创建 API 密钥")
    public Result<Long> createApiKey( @RequestBody AiApiKeySaveReqVO createReqVO) {
        return Result.success(apiKeyService.createApiKey(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新 API 密钥")
    public Result<Boolean> updateApiKey( @RequestBody AiApiKeySaveReqVO updateReqVO) {
        return  Result.success(apiKeyService.updateApiKey(updateReqVO));
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除 API 密钥")
    public Result<Boolean> deleteApiKey(@RequestParam("id") Long id) {

        return  Result.success(apiKeyService.deleteApiKey(id));
    }

    @GetMapping("/get")
    @Operation(summary = "获得 API 密钥")

    public Result<AiApiKey> getApiKey(@RequestParam("id") Long id) {
        AiApiKey apiKey = apiKeyService.getById(id);
        return Result.success(apiKey);
    }

    @GetMapping("/page")
    @Operation(summary = "获得 API 密钥分页")
    public Result<PageResult<AiApiKey>> getApiKeyPage(@Valid AiApiKeyPageReqVO pageReqVO) {
        PageResult<AiApiKey> pageResult = apiKeyService.getApiKeyPage(pageReqVO);
        return Result.success(pageResult);
    }

}
