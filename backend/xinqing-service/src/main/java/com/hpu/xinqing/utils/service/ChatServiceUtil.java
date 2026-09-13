package com.hpu.xinqing.utils.service;


import com.hpu.xinqing.utils.common.ObjToObj;
import com.hpu.xinqingpojo.entity.Chat;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class ChatServiceUtil {


    public Map<String,Object> toStandard(Chat chat, Long myId){
        Map<String, Object> res = ObjToObj.objectToMap(chat);
        res.remove("id");
        res.remove("userSenderId");
        res.remove("userReceiverId");
        res.remove("serialVersionUID");
        if (chat.getUserSenderId().equals(myId)){
            res.put("sender","me");
        }else res.put("sender","other");
        return res;
    }

    public List<Map<String,Object>> toStandard(List<Chat> chats,Long myId){
        return chats.stream()
                .map(chat -> toStandard(chat,myId))
                .toList();
    }
}
