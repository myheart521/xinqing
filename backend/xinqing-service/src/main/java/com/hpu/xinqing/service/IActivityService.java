package com.hpu.xinqing.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.hpu.xinqingcommon.result.Result;
import com.hpu.xinqingpojo.DTO.ActivityDTO;
import com.hpu.xinqingpojo.entity.Activity;

import java.util.List;

/**
 * <p>
 * 活动 服务类
 * </p>
 *
 * @since 2025-02-18
 */
public interface IActivityService extends IService<Activity> {

    Result add(ActivityDTO activityDTO);

    Result delectById(Long id);

    Result selectById(Long id);

    Result selectAll(Integer current);


    /**
     * 查询用户参与的活动
     */
    List<Activity> selectActiveListByUserId(Long id);

    /**
     * 查询用户参与活动，可以指明查询几条
     */
    List<Activity> selectActiveListByUserId(Long id, Integer limit);

    /**
     * 查询用户参与活动的数量
     */
    Long selectActiveCountByUserId(Long id);
}
