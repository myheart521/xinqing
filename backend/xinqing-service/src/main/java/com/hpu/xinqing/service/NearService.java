package com.hpu.xinqing.service;


import com.hpu.xinqingcommon.result.Result;

/**
 * <p>
 * 附近 服务类
 * </p>
 *
 * @since 2025-02-18
 */
public interface NearService {

    Result queryUserByGeo(Double longitude, Double latitude);

    Result queryBlogByGeo( Integer current, Double longitude, Double latitude);
}
