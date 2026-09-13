package com.hpu.xinqing.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.hpu.xinqingcommon.result.Result;
import com.hpu.xinqingpojo.entity.Circle;

/**
 * <p>
 * 圈子 服务类
 * </p>
 *
 * @since 2025-02-18
 */
public interface ICircleService extends IService<Circle> {

    Result getTop();

    Result getAll();

    Result queryById(Long id);


    /**
     * 根据id获取圈子的名字
     */

    String getCircleNameById(Long id);
}
