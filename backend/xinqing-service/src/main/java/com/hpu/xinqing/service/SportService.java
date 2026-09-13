package com.hpu.xinqing.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hpu.xinqingpojo.DTO.SportDTO;
import com.hpu.xinqingpojo.VO.SportVO;
import com.hpu.xinqingpojo.VO.SportVideoVO;
import com.hpu.xinqingpojo.entity.Sport;

import java.util.List;

public interface SportService  {

    /**
     * 运动查询
     * @param sportDTO
     * @return
     */
    List<SportVO> select(SportDTO sportDTO);

    /**
     * 查询运动详细信息
     * @param id
     * @return
     */
    SportVideoVO selectById(Long id);
}
