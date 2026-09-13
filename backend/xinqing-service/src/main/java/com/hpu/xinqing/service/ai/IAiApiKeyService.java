package com.hpu.xinqing.service.ai;


import com.baomidou.mybatisplus.extension.service.IService;
import com.hpu.xinqingcommon.result.PageResult;
import com.hpu.xinqingpojo.VO.AiApiKeyPageReqVO;
import com.hpu.xinqingpojo.VO.AiApiKeySaveReqVO;
import com.hpu.xinqingpojo.entity.AiApiKey;

/**
 * <p>
 * AI API 密钥表 服务类
 * </p>
 *
 * @since 2025-04-08
 */
public interface IAiApiKeyService extends IService<AiApiKey> {

    Long createApiKey(AiApiKeySaveReqVO createReqVO);

    PageResult<AiApiKey> getApiKeyPage(AiApiKeyPageReqVO pageReqVO);

    Boolean deleteApiKey(Long id);

    Boolean updateApiKey(AiApiKeySaveReqVO updateReqVO);

    AiApiKey getRequiredDefaultApiKey(String platform, Integer status);

    AiApiKey validateApiKey(Long keyId);


//     AiApiKey validateApiKey(Long id);
}
