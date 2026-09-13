package com.hpu.xinqing.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.hpu.xinqingpojo.DTO.PageDTO;
import com.hpu.xinqingpojo.VO.ModulesPageVO;
import com.hpu.xinqingpojo.entity.Activity;
import com.hpu.xinqingpojo.entity.Modules;

import java.util.List;

public interface ModulesService {
    List<ModulesPageVO> getTestByPages(PageDTO pageDTO);


    /**
     * 根据id获取测评的类型
     */
    Modules getTestTypeById(Long id);
}
