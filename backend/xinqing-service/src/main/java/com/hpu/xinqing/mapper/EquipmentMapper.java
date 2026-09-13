package com.hpu.xinqing.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hpu.xinqingpojo.entity.Equipment;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @since 2024-10-27
 */
public interface EquipmentMapper extends BaseMapper<Equipment> {

    List<Equipment> selectByEquipType(String type);
}
