package com.hpu.xinqing.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.hpu.xinqingcommon.result.Result;
import com.hpu.xinqingpojo.entity.Tag;

/**
 * <p>
 * 标签 服务类
 * </p>
 *
 * @since 2025-02-18
 */
public interface ITagService extends IService<Tag> {

    Result getTop();

    Result getAll();
}
