package com.hpu.xinqing.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hpu.xinqingpojo.VO.DataVo;
import com.hpu.xinqingpojo.entity.HistoryTest;

import java.util.List;

public interface HistoryTestMapper extends BaseMapper<HistoryTest> {
    List<DataVo> selectTopStudentsByTestCount();
}
