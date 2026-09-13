package com.hpu.xinqing.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.hpu.xinqingcommon.result.PageResult;
import com.hpu.xinqingcommon.result.Result;
import com.hpu.xinqingpojo.entity.ActivityFollow;

/**
 * <p>
 * 活动参与表 服务类
 * </p>
 *
 * @since 2025-02-27
 */
public interface IActivityFollowService extends IService<ActivityFollow> {

    Boolean queryFollow(Long id);

    Result follow(Long id, Boolean isFollow);

    PageResult queryFollowList(Integer current);
}
