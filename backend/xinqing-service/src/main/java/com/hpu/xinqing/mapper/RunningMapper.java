package com.hpu.xinqing.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hpu.xinqingpojo.DTO.RunningReportDTO;
import com.hpu.xinqingpojo.entity.Running;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @since 2024-10-23
 */
public interface RunningMapper extends BaseMapper<Running> {

    List<RunningReportDTO> selectCountByDate(Map map);
}
