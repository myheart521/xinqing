package com.hpu.xinqing.service.serviceImpl;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hpu.xinqing.mapper.SleepAudiosMapper;
import com.hpu.xinqing.service.ISleepAudiosService;
import com.hpu.xinqing.service.ITypeService;
import com.hpu.xinqingcommon.constant.TypeContants;
import com.hpu.xinqingpojo.VO.SleepAudiosVO;
import com.hpu.xinqingpojo.entity.SleepAudios;
import com.hpu.xinqingpojo.entity.Type;
import com.hpu.xinqingpojo.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 助眠 服务实现类
 * </p>
 *
 * @since 2025-03-15
 */
@Service
public class SleepAudiosServiceImpl extends ServiceImpl<SleepAudiosMapper, SleepAudios> implements ISleepAudiosService {

    @Autowired
    private ITypeService typeService;


    @Override
    public List<SleepAudiosVO> selectAll() {
        List<Type> types = typeService.selectBySort(TypeContants.SLEEP);
        List<Integer> typeIds = types.stream().map(Type::getId).collect(Collectors.toList());
        List<SleepAudios> typeIdList = query().in("type_id", typeIds).list();
        Map<Integer, List<SleepAudios>> collect = typeIdList.stream().collect(Collectors.groupingBy(SleepAudios::getTypeId));
        List<SleepAudiosVO> list=new ArrayList<>();
        for (Type type : types) {
            SleepAudiosVO sleepAudiosVO = new SleepAudiosVO();
            sleepAudiosVO.setTypeId(type.getId());
            sleepAudiosVO.setList(collect.get(type.getId()));
            list.add(sleepAudiosVO);
        }
        return list;

    }
}
