package com.hpu.xinqing.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hpu.xinqingpojo.DTO.BasePageDTO;
import com.hpu.xinqingpojo.DTO.PageDTO;
import com.hpu.xinqingpojo.VO.DietVO;
import com.hpu.xinqingpojo.entity.Diet;

import java.util.List;
import java.util.Map;


public interface DietService extends IService<Diet> {

    List<DietVO> pageQuery(PageDTO pageDTO);




    //---------------如下是DAO
    List<Diet> getByUserId(Long userId, BasePageDTO basePageDTO);
    List<Map> getMapByUserId(Long userId, BasePageDTO basePageDTO);
}
