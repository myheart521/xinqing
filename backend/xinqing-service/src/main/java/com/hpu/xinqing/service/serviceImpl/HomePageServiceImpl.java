package com.hpu.xinqing.service.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hpu.xinqing.mapper.BannerMapper;
import com.hpu.xinqing.mapper.SwiperMapper;
import com.hpu.xinqing.service.HomePageService;
import com.hpu.xinqingpojo.DTO.PageDTO;
import com.hpu.xinqingpojo.VO.BannerPageVO;
import com.hpu.xinqingpojo.VO.SwiperPageVO;
import com.hpu.xinqingpojo.entity.Banner;
import com.hpu.xinqingpojo.entity.Swiper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class HomePageServiceImpl implements HomePageService {
    @Autowired
    private BannerMapper bannerMapper;

    @Autowired
    private SwiperMapper swiperMapper;

    @Override
    public List<BannerPageVO> pageBanners(PageDTO pageDTO) {
        Integer pageSize = pageDTO.getPageSize();
        Integer pageNo = pageDTO.getPageNo();
        //添加分页基本参数
        Page<Banner> bannerPage = new Page<>(pageNo, pageSize);
        //设置排序参数，按照时间顺序，升序
        OrderItem orderItem1 = new OrderItem().setAsc(true).setColumn("create_time");
        bannerPage.addOrder(orderItem1);
        List<String> list = List.of(new String[]{"image_url","link"});
        //去数据库查
        Page<Banner> page = bannerMapper.selectPage(bannerPage,new QueryWrapper<Banner>().select(list));
        List<BannerPageVO> records = page.getRecords().stream().map((banner -> new BannerPageVO(banner.getImageUrl(),banner.getLink()))).collect(Collectors.toList());
        return records;
    }

    @Override
    public List<SwiperPageVO> pageSwiper(PageDTO pageDTO) {
        Integer pageSize = pageDTO.getPageSize();
        Integer pageNo = pageDTO.getPageNo();
        //添加分页基本参数
        Page<Swiper> swiperPage = new Page<>(pageNo, pageSize);
        //设置排序参数，按照时间顺序，升序
        OrderItem orderItem1 = new OrderItem().setAsc(true).setColumn("create_time");
        swiperPage.addOrder(orderItem1);
        List<String> list = List.of(new String[]{"url","name","text","link"});
        //去数据库查
        Page<Swiper> page = swiperMapper.selectPage(swiperPage,new QueryWrapper<Swiper>().select(list));
        List<SwiperPageVO> records = page.getRecords().stream().map((swiper -> new SwiperPageVO(swiper.getUrl(),swiper.getName(),swiper.getText(),swiper.getLink()))).collect(Collectors.toList());
        return records;
    }
}
