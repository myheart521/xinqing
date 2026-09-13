package com.hpu.xinqing.service;

import com.hpu.xinqingpojo.entity.UserAndSport;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @description 针对表【user_and_sport】的数据库操作Service
* @createDate 2025-04-05 21:10:13
*/
public interface UserAndSportService extends IService<UserAndSport> {

    void watch(Long userId, Long sportId);

    List<UserAndSport> watchHistory(Long userId);

    List<UserAndSport> watchHistoryByScale(Long userId,Integer scale);
}
