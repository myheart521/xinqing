package com.hpu.xinqing.service.serviceImpl.ai;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hpu.xinqing.factory.AiModelFactory;
import com.hpu.xinqing.mapper.AiModelMapper;
import com.hpu.xinqing.service.ai.IAiApiKeyService;
import com.hpu.xinqing.service.ai.IAiModelService;
import com.hpu.xinqingcommon.result.PageResult;
import com.hpu.xinqingpojo.VO.AiModelPageReqVO;
import com.hpu.xinqingpojo.VO.AiModelSaveReqVO;
import com.hpu.xinqingpojo.enmus.AiPlatformEnum;
import com.hpu.xinqingpojo.entity.AiApiKey;
import com.hpu.xinqingpojo.entity.AiModel;
import dev.langchain4j.model.chat.ChatLanguageModel;
import jakarta.annotation.Resource;
import opennlp.tools.util.StringUtil;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.image.ImageModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * AI 模型表 服务实现类
 * </p>
 *
 * @since 2025-04-08
 */
@Service
public class AiModelServiceImpl extends ServiceImpl<AiModelMapper, AiModel> implements IAiModelService {

    @Resource
    private IAiApiKeyService apiKeyService;

    @Resource
    private AiModelMapper modelMapper;

    @Resource
    private AiModelFactory modelFactory;


    @Override
    public Long createModel(AiModelSaveReqVO createReqVO) {
        // 1. 校验
        AiPlatformEnum.validatePlatform(createReqVO.getPlatform());
        apiKeyService.validateApiKey(createReqVO.getKeyId());

        // 2. 插入
        AiModel model = BeanUtil.toBean(createReqVO, AiModel.class);
        modelMapper.insert(model);
        return model.getId();
    }

    @Override
    public Boolean updateModel(AiModelSaveReqVO updateReqVO) {
        // 1. 校验
        validateModelExists(updateReqVO.getId());
        AiPlatformEnum.validatePlatform(updateReqVO.getPlatform());
        apiKeyService.validateApiKey(updateReqVO.getKeyId());

        // 2. 更新
        AiModel updateObj = BeanUtil.toBean(updateReqVO, AiModel.class);
       return updateById(updateObj);
    }

    @Override
    public void deleteModel(Long id) {
        // 校验存在
        validateModelExists(id);
        // 删除
        removeById(id);
    }

    private AiModel validateModelExists(Long id) {
        AiModel model = modelMapper.selectById(id);
        if (modelMapper.selectById(id) == null) {
            throw new RuntimeException("模型不存在");
        }
        return model;
    }

    @Override
    public AiModel getModel(Long id) {
        return modelMapper.selectById(id);
    }

    @Override
    public PageResult<AiModel> getModelPage(AiModelPageReqVO pageReqVO) {
        Page<AiModel> page = lambdaQuery().eq(StrUtil.isNotBlank(pageReqVO.getPlatform()), AiModel::getPlatform, pageReqVO.getPlatform())
                .like(StrUtil.isNotBlank(pageReqVO.getName()), AiModel::getName, pageReqVO.getName())
                .eq(StrUtil.isNotBlank(pageReqVO.getModel()), AiModel::getModel, pageReqVO.getModel())
                .page(new Page<>(pageReqVO.getPageNo(), pageReqVO.getPageSize()));
        if (page.getRecords().isEmpty()) {
            return PageResult.isEmpty();
        }
        return new PageResult<AiModel>(page.getTotal(), page.getRecords());
    }

    @Override
    public AiModel validateModel(Long id) {
        AiModel model = validateModelExists(id);
        if (model.getStatus()==0) {
            throw new RuntimeException("模型被禁用");
        }
        return model;
    }


    // ========== 与 LangChain4j 集成 ==========

    @Override
    public ChatLanguageModel getChatModel(Long id) {
        AiModel model = validateModel(id);
        AiApiKey apiKey = apiKeyService.validateApiKey(model.getKeyId());
        AiPlatformEnum platform = AiPlatformEnum.validatePlatform(apiKey.getPlatform());
        return modelFactory.getOrCreateChatModel(platform, apiKey.getApiKey(), model.getModel(),apiKey.getUrl());
    }

}
