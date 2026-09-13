package com.hpu.xinqing.service.serviceImpl.ai;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hpu.xinqing.mapper.AiChatMemoryMapper;
import com.hpu.xinqing.service.ai.IAiChatMemoryService;
import com.hpu.xinqingcommon.utils.StpUtils;
import com.hpu.xinqingpojo.entity.AiChatMemory;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 聊天回话表 服务实现类
 * </p>
 *
 * @since 2025-03-20
 */
@Service
public class AiChatMemoryServiceImpl extends ServiceImpl<AiChatMemoryMapper, AiChatMemory> implements IAiChatMemoryService {

    /**
     * 新增并更新回话
     * @param memoryId
     * @param prompt
     */
    @Override
    @Transactional//事务管理
    public void createAndUpdate(String memoryId, String prompt) {
        AiChatMemory byId = getById(memoryId);
        //不为空只需要更新时间
        if(byId!=null){
            update().set("update_time", LocalDateTime.now()).update();
        }else {
            //为空需要创建
            AiChatMemory aiChatMemory = new AiChatMemory();
            aiChatMemory.setId(memoryId);
            aiChatMemory.setUserId(StpUtils.userId());
            aiChatMemory.setName(prompt);
            save(aiChatMemory);
        }
    }

    /**
     * 根据userid查询用户会话
     * @return
     */
    @Override
    public List<AiChatMemory> getByUserId() {
        Long userId;
        try{
            userId = StpUtils.userId();
        }catch (Exception ex){
            throw new RuntimeException("请先登录");
        }
        List<AiChatMemory> list = lambdaQuery().eq(AiChatMemory::getUserId, userId).orderByDesc(AiChatMemory::getUpdateTime).list();
        return list;
    }

    @Override
    public List<AiChatMemory> getByOtherUserId(Long userId) {
        LocalDateTime endTime = LocalDateTime.now();
        //一周前
        LocalDateTime startTime = endTime.minusWeeks(1);

        return lambdaQuery().select(AiChatMemory::getId,AiChatMemory::getName)
                .eq(AiChatMemory::getUserId, userId)
                .between(AiChatMemory::getUpdateTime, startTime, endTime)
                .orderByDesc(AiChatMemory::getUpdateTime)
                .list();
    }
}
