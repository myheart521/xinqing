package com.hpu.xinqing.utils.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hpu.xinqing.mapper.ChatMapper;
import com.hpu.xinqingpojo.entity.Chat;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ChatMapperUtil {


    @Resource
    ChatMapper chatMapper;

    public Page<Chat> getHistoryPage(Long senderId, Long receiverId, Page<Chat> page){
        QueryWrapper<Chat> wrapper = new QueryWrapper<>();
        wrapper.eq("user_sender_id",senderId)
                .eq("user_receiver_id",receiverId)
                .or(w->w.eq("user_sender_id",receiverId)
                                .eq("user_receiver_id",senderId));
        page.setOrders(List.of(OrderItem.desc("create_time")));
        return chatMapper.selectPage(page,wrapper);
    }

    public List<Chat> getHistory(Long senderId, Long receiverId,Page<Chat> page){
        return getHistoryPage(senderId,receiverId,page).getRecords();
    }

}