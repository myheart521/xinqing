package com.hpu.xinqing.service;


import com.hpu.xinqingpojo.DTO.BasePageDTO;
import com.hpu.xinqingpojo.DTO.DietDTO;
import com.hpu.xinqingpojo.DTO.NewKnowledgeDTO;
import com.hpu.xinqingpojo.entity.Banner;
import com.hpu.xinqingpojo.entity.User;

import java.util.List;
import java.util.Map;

public interface TeacherService {

    Map<String, Object> getStuInfo(Long stuId, List<String> params);

    void publish(NewKnowledgeDTO article);

    void publishDiet(DietDTO dietDTO);

    void publishBanner(Banner banner);

    List<Map> getArticle(Long userId,BasePageDTO pageDTO, String params);

    List<User> getStuList(Long userId);
}
