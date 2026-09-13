package com.hpu.xinqing.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.hpu.xinqingcommon.result.PageResult;
import com.hpu.xinqingcommon.result.Result;
import com.hpu.xinqingpojo.entity.Follow;

/**
 * <p>
 * 用户之间关注信息 服务类
 * </p>
 *
 * @since 2025-02-22
 */
public interface IFollowService extends IService<Follow> {
    Boolean queryFollow(Long id);

    Result follow(Long id, Boolean isFollow);


    PageResult followerList(Integer current);
}
