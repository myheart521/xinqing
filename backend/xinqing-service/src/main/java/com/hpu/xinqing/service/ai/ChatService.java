package com.hpu.xinqing.service.ai;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hpu.xinqingcommon.result.Result;
import com.hpu.xinqingpojo.entity.Chat;

/**
* @description 针对表【chat】的数据库操作Service
* @createDate 2024-12-19 10:51:57
*/
public interface ChatService extends IService<Chat> {
    Result<?> getHistory(Long senderId, Long receiverId, Page<Chat> page);
}