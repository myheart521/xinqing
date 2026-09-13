package com.hpu.xinqing.service.serviceImpl;

import com.hpu.xinqing.mapper.TypeMapper;
import com.hpu.xinqing.service.ITypeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hpu.xinqingcommon.constant.TypeContants;
import com.hpu.xinqingpojo.entity.Type;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 分类表 服务实现类
 * </p>
 *
 * @since 2025-03-15
 */
@Service
public class TypeServiceImpl extends ServiceImpl<TypeMapper, Type> implements ITypeService {

    @Override
    public List<Type> selectBySort(int type) {
        return  lambdaQuery().eq(Type::getSort, type).list();

    }
}
