package com.hpu.xinqing.service.serviceImpl.ai;


import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hpu.xinqing.mapper.AiApiKeyMapper;
import com.hpu.xinqing.service.ai.IAiApiKeyService;
import com.hpu.xinqingcommon.result.PageResult;
import com.hpu.xinqingpojo.VO.AiApiKeyPageReqVO;
import com.hpu.xinqingpojo.VO.AiApiKeySaveReqVO;
import com.hpu.xinqingpojo.entity.AiApiKey;
import jakarta.annotation.Resource;
import opennlp.tools.util.StringUtil;
import org.springframework.stereotype.Service;


/**
 * <p>
 * AI API 密钥表 服务实现类
 * </p>
 *
 * @since 2025-04-08
 */
@Service
public class AiApiKeyServiceImpl extends ServiceImpl<AiApiKeyMapper, AiApiKey> implements IAiApiKeyService {

    @Resource
    private AiApiKeyMapper apiKeyMapper;

    @Override
    public Long createApiKey(AiApiKeySaveReqVO createReqVO) {
        // 插入
        AiApiKey apiKey = BeanUtil.toBean(createReqVO, AiApiKey.class);
        boolean save = save(apiKey);
        if (!save) {
            throw new RuntimeException("添加失败");
        }
        // 返回
        return apiKey.getId();
    }

    @Override
    public Boolean updateApiKey(AiApiKeySaveReqVO updateReqVO) {
        // 校验存在
        validateApiKeyExists(updateReqVO.getId());
        // 更新
        AiApiKey updateObj = BeanUtil.copyProperties(updateReqVO, AiApiKey.class);
        return updateById(updateObj);
    }

    @Override
    public Boolean deleteApiKey(Long id) {
        // 校验存在
        validateApiKeyExists(id);
        // 删除
       return removeById(id);
    }

    private AiApiKey validateApiKeyExists(Long id) {
        AiApiKey apiKey = apiKeyMapper.selectById(id);
        if (apiKey == null) {
            throw new RuntimeException("apiKey不存在");
        }
        return apiKey;
    }


    @Override
    public PageResult<AiApiKey> getApiKeyPage(AiApiKeyPageReqVO pageReqVO) {
        Page<AiApiKey> page = lambdaQuery().like(!StringUtil.isEmpty(pageReqVO.getName()), AiApiKey::getName, pageReqVO.getName())
                .eq(!StringUtil.isEmpty(pageReqVO.getPlatform()), AiApiKey::getPlatform, pageReqVO.getPlatform())
                .eq(pageReqVO.getStatus() != null, AiApiKey::getStatus, pageReqVO.getStatus())
                .orderByDesc(AiApiKey::getId)
                .page(new Page<>(pageReqVO.getPageNo(), pageReqVO.getPageSize()));
        if(page.getRecords().isEmpty()){
            return PageResult.isEmpty();
        }
        return new PageResult<>(page.getTotal(), page.getRecords());
    }


    @Override
    public AiApiKey getRequiredDefaultApiKey(String platform, Integer status) {
        AiApiKey apiKey = lambdaQuery()
                .eq(!StringUtil.isEmpty(platform), AiApiKey::getPlatform, platform)
                .eq(status != null, AiApiKey::getStatus, status)
                .one();
        if (apiKey == null) {
            throw new RuntimeException("默认 API 密钥不存在");
        }
        return apiKey;
    }

    @Override
    public AiApiKey validateApiKey(Long id) {
        AiApiKey apiKey = validateApiKeyExists(id);
        if (apiKey.getStatus()!=1) {
            throw new RuntimeException("apiKey已被禁用");
        }
        return apiKey;
    }
}
