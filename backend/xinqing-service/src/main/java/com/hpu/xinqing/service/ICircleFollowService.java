package com.hpu.xinqing.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.hpu.xinqingcommon.result.PageResult;
import com.hpu.xinqingcommon.result.Result;
import com.hpu.xinqingpojo.entity.CircleFollow;

/**
 * <p>
 * 用户关注圈子信息表 服务类
 * </p>
 *
 * @since 2025-02-22
 */
public interface ICircleFollowService extends IService<CircleFollow> {

    Boolean queryFollow(Long id);

    Result follow(Long id, Boolean isFollow);

    PageResult selectList(Integer current);
}
