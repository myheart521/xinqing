package com.hpu.xinqing.controller.ai;


import cn.hutool.core.bean.BeanUtil;
import com.hpu.xinqing.service.ai.IAiModelService;
import com.hpu.xinqingcommon.result.PageResult;
import com.hpu.xinqingcommon.result.Result;
import com.hpu.xinqingpojo.VO.AiModelPageReqVO;
import com.hpu.xinqingpojo.VO.AiModelSaveReqVO;
import com.hpu.xinqingpojo.entity.AiModel;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * AI 模型表 前端控制器
 * </p>
 *
 * @since 2025-04-08
 */
@RestController
@RequestMapping("/ai-model")
public class AiModelController {
    @Resource
    private IAiModelService modelService;

    @PostMapping("/create")
    @Operation(summary = "创建模型")
    public Result<Long> createModel( @RequestBody AiModelSaveReqVO createReqVO) {
        return Result.success(modelService.createModel(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新模型")
    public Result<Boolean> updateModel( @RequestBody AiModelSaveReqVO updateReqVO) {

        return Result.success( modelService.updateModel(updateReqVO));
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除模型")
    public Result<Boolean> deleteModel(@RequestParam("id") Long id) {
        modelService.deleteModel(id);
        return Result.success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得模型")
    public Result<AiModel> getModel(@RequestParam("id") Long id) {
        AiModel model = modelService.getById(id);
        return Result.success(model);
    }

    @GetMapping("/page")
    @Operation(summary = "获得模型分页")
    public Result<PageResult<AiModel>> getModelPage(AiModelPageReqVO pageReqVO) {
        PageResult<AiModel> pageResult = modelService.getModelPage(pageReqVO);
        return Result.success(pageResult);
    }
}
