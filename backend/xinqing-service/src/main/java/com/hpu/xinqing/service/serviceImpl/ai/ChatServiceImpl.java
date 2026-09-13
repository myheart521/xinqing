package com.hpu.xinqing.service.serviceImpl.ai;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hpu.xinqing.mapper.ChatMapper;
import com.hpu.xinqing.mapper.UserMapper;
import com.hpu.xinqing.service.ai.ChatService;
import com.hpu.xinqing.utils.mapper.ChatMapperUtil;
import com.hpu.xinqing.utils.service.ChatServiceUtil;
import com.hpu.xinqingcommon.result.Result;
import com.hpu.xinqingpojo.entity.Chat;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @description 针对表【chat】的数据库操作Service实现
 * @createDate 2024-12-19 10:51:57
 */
@Service
public class ChatServiceImpl extends ServiceImpl<ChatMapper, Chat>
        implements ChatService {
    @Resource
    ChatMapper chatMapper;
    @Resource
    UserMapper userMapper;
    @Autowired
    ChatMapperUtil chatMapperUtil;
    @Autowired
    ChatServiceUtil chatServiceUtil;

    /**
     *{
     *     history:[],
     *     page:{
     *          total: num,
     *          current: num
     *     }
     *}
     */
    @Override
    public Result<?> getHistory(Long senderId, Long receiverId, Page<Chat> page) {
        Page<Chat> historyPage = chatMapperUtil.getHistoryPage(senderId, receiverId, page);
        List<Map<String, Object>> chatContent = chatServiceUtil.toStandard(historyPage.getRecords(), senderId);
        return Result.success(Map.of("history",chatContent,
                "page",
                Map.of("total",historyPage.getPages(),
                        "current",historyPage.getCurrent())));
    }
}
