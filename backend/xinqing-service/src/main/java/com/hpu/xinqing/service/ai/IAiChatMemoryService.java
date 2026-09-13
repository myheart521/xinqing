package com.hpu.xinqing.service.ai;


import com.baomidou.mybatisplus.extension.service.IService;
import com.hpu.xinqingpojo.entity.AiChatMemory;

import java.util.List;

/**
 * <p>
 * 聊天回话表 服务类
 * </p>
 *
 * @since 2025-03-20
 */
public interface IAiChatMemoryService extends IService<AiChatMemory> {

    void createAndUpdate(String memoryId, String prompt);

    List<AiChatMemory> getByUserId();

    List<AiChatMemory> getByOtherUserId(Long userId);
}
