package com.hpu.xinqing.controller;

import com.hpu.xinqing.service.HomePageService;
import com.hpu.xinqingcommon.result.Result;
import com.hpu.xinqingpojo.DTO.PageDTO;
import com.hpu.xinqingpojo.VO.BannerPageVO;
import com.hpu.xinqingpojo.VO.NewKnowledgeListVO;
import com.hpu.xinqingpojo.VO.SwiperPageVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


/**
 *首页管理
 **/
@RestController
@RequestMapping("/home")
@Slf4j
public class HomePageController {
    @Autowired
    private HomePageService homePageService;

    @GetMapping("/banners")
    public Result getBanner(PageDTO pageDTO){
        log.info("获取上方{}",pageDTO);
        List<BannerPageVO> bannerPagesVOS= homePageService.pageBanners(pageDTO);
        return Result.success(bannerPagesVOS);
    }
    @GetMapping("/swiper")
    public Result getSwiper(PageDTO pageDTO){
        log.info("获取轮播图{}",pageDTO);
        List<SwiperPageVO> swiperPagesVOS= homePageService.pageSwiper(pageDTO);
        return Result.success(swiperPagesVOS);
    }
}
