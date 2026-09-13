package com.hpu.xinqing.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hpu.xinqingpojo.entity.MbtiTopic;
import com.hpu.xinqingpojo.VO.MbtiTestVO;

import java.util.List;

public interface MbtiService extends IService<MbtiTopic> {
    //查找全部
    List<MbtiTopic> findAll();
    // 计算答案
    MbtiTestVO calculateAns(String ans);
}
