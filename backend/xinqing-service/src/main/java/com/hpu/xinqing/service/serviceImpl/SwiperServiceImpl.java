package com.hpu.xinqing.service.serviceImpl;

import com.hpu.xinqing.mapper.SwiperMapper;
import com.hpu.xinqing.service.SwiperService;
import com.hpu.xinqingpojo.DTO.SwiperDTO;
import com.hpu.xinqingpojo.entity.Swiper;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class SwiperServiceImpl implements SwiperService {

    @Resource
    SwiperMapper swiperMapper;


    @Override
    public void publishCard(SwiperDTO swiperDTO) {
        Swiper swiper = new Swiper();
        BeanUtils.copyProperties(swiperDTO, swiper);
        swiperMapper.insert(swiper);
    }
}
