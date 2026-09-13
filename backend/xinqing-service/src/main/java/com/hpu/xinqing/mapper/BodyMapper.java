package com.hpu.xinqing.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hpu.xinqingpojo.DTO.BodyReportDTO;
import com.hpu.xinqingpojo.entity.Body;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 体脂秤 Mapper 接口
 * </p>
 *
 * @since 2024-10-23
 */
public interface BodyMapper extends BaseMapper<Body> {

    List<BodyReportDTO> avgBody(Map map);
}
