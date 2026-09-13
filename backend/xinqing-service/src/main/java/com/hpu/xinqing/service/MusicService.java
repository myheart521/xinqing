package com.hpu.xinqing.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hpu.xinqingpojo.DTO.PageDTO;
import com.hpu.xinqingpojo.VO.MusicVo;
import com.hpu.xinqingpojo.entity.Music;


import java.util.List;

public interface MusicService extends IService<Music> {

    List<MusicVo> pageQuery(PageDTO pageDTO);
}
