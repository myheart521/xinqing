package com.hpu.xinqing.service;

import com.hpu.xinqingpojo.DTO.PageDTO;
import com.hpu.xinqingpojo.VO.BannerPageVO;
import com.hpu.xinqingpojo.VO.SwiperPageVO;

import java.util.List;

public interface HomePageService{

    List<BannerPageVO> pageBanners(PageDTO pageDTO);

    List<SwiperPageVO> pageSwiper(PageDTO pageDTO);
}
