package com.hpu.xinqing.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hpu.xinqingpojo.DTO.SmartwatchReportDTO;
import com.hpu.xinqingpojo.entity.Smartwatch;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @since 2024-10-23
 */
public interface SmartwatchMapper extends BaseMapper<Smartwatch> {

    List<SmartwatchReportDTO> avgSmartwatch(Map map);
}
