package com.hpu.xinqing.service;

import com.hpu.xinqingpojo.VO.*;

import java.util.List;

public interface StatisticsService {
    List<StatsCardVo> getStatsCards();

    List<DataVo> getPieData();

    List<DataVo> getTeacherCommData();

    List<DataVo> getEvaluationData();

    List<DataVo> getActivityData();

    List<DataVo> getPostData();

    LineVo getLine();

    RadarVo getRadar();

    List<CollegePointsVo> getCollegePoints();
}
