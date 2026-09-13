package com.hpu.xinqing.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hpu.xinqingpojo.VO.ActivityAllVO;
import com.hpu.xinqingpojo.entity.Activity;

/**
 * <p>
 * 活动 Mapper 接口
 * </p>
 *
 * @since 2025-02-18
 */
public interface ActivityMapper extends BaseMapper<Activity> {

    IPage<ActivityAllVO> selectOrderByHot(IPage<ActivityAllVO> page, int view, int follow, double timeK);
}
