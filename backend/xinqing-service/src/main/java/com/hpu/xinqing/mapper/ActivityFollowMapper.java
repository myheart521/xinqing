package com.hpu.xinqing.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hpu.xinqingpojo.VO.DataVo;
import com.hpu.xinqingpojo.entity.ActivityFollow;

import java.util.List;

/**
 * <p>
 * 活动参与表 Mapper 接口
 * </p>
 *
 * @since 2025-02-27
 */
public interface ActivityFollowMapper extends BaseMapper<ActivityFollow> {
    List<DataVo> selectTopParticipants();
}
