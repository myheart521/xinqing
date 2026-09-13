package com.hpu.xinqing.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.hpu.xinqingpojo.entity.Type;

import java.util.List;

/**
 * <p>
 * 分类表 服务类
 * </p>
 *
 * @since 2025-03-15
 */
public interface ITypeService extends IService<Type> {

    List<Type> selectBySort(int type);
}
