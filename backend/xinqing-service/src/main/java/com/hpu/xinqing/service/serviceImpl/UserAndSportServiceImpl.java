package com.hpu.xinqing.service.serviceImpl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hpu.xinqing.mapper.UserAndSportMapper;
import com.hpu.xinqing.service.SportService;
import com.hpu.xinqing.service.UserAndSportService;
import com.hpu.xinqing.service.UserService;
import com.hpu.xinqingpojo.entity.UserAndSport;

import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
* @description 针对表【user_and_sport】的数据库操作Service实现
* @createDate 2025-04-05 21:10:13
*/
@Service
public class UserAndSportServiceImpl extends ServiceImpl<UserAndSportMapper, UserAndSport>
    implements UserAndSportService {

    @Resource
    UserAndSportMapper userAndSportMapper;
    @Autowired
    SportService sportService;
    @Autowired
    UserService userService;


    @Override
    public void watch(Long userId, Long sportId) {
        UserAndSport userAndSport = new UserAndSport(userId, sportId);
        userAndSportMapper.insert(userAndSport);
    }

    @Override
    public List<UserAndSport> watchHistory(Long userId) {
        return lambdaQuery()
                .eq(UserAndSport::getUserId, userId)
                .list();
    }

    /**
     * -1 表示全部
     */
    @Override
    public List<UserAndSport> watchHistoryByScale(Long userId, Integer scale) {
        LocalDateTime localDateTime = LocalDateTime.now().minusDays(scale);
        return lambdaQuery()
                .eq(UserAndSport::getUserId, userId)
                .ge(UserAndSport::getCreateTime,localDateTime)
                .list();
    }
}




