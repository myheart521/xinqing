package com.hpu.xinqing.service.ai;


import com.baomidou.mybatisplus.extension.service.IService;
import com.hpu.xinqingcommon.result.PageResult;
import com.hpu.xinqingpojo.VO.AiModelPageReqVO;
import com.hpu.xinqingpojo.VO.AiModelSaveReqVO;
import com.hpu.xinqingpojo.entity.AiModel;
import dev.langchain4j.community.model.dashscope.QwenChatModel;
import dev.langchain4j.model.chat.ChatLanguageModel;

/**
 * <p>
 * AI 模型表 服务类
 * </p>
 *
 * @since 2025-04-08
 */
public interface IAiModelService extends IService<AiModel> {

    Long createModel(AiModelSaveReqVO createReqVO);

    Boolean updateModel(AiModelSaveReqVO updateReqVO);

    void deleteModel(Long id);

    PageResult<AiModel> getModelPage(AiModelPageReqVO pageReqVO);

    AiModel getModel(Long id);

    AiModel validateModel(Long id);

    ChatLanguageModel getChatModel(Long id);
}
