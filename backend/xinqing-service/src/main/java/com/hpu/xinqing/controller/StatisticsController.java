package com.hpu.xinqing.controller;


import com.hpu.xinqing.service.StatisticsService;
import com.hpu.xinqingcommon.result.Result;
import com.hpu.xinqingpojo.VO.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("static")
@Slf4j
public class StatisticsController {
    @Autowired
    private StatisticsService statisticsService;

    @GetMapping("statsCards")
    public Result statsCards(){

        List<StatsCardVo> statsCards = statisticsService.getStatsCards();

        return Result.success(statsCards);
    }

    @GetMapping("pieData")
    public Result pieData(){
        List<DataVo> dataVos = statisticsService.getPieData();
        return Result.success(dataVos);
    }
    @GetMapping("teacherCommData")
    public Result teacherCommData(){
        List<DataVo> dataVos = statisticsService.getTeacherCommData();
        return Result.success(dataVos);
    }

    @GetMapping("evaluationData")
    public Result evaluationData(){
        List<DataVo> dataVos = statisticsService.getEvaluationData();
        return Result.success(dataVos);
    }

    @GetMapping("postData")
    public Result postData(){
        List<DataVo> dataVos = statisticsService.getPostData();
        return Result.success(dataVos);
    }

    @GetMapping("activityData")
    public Result activityData(){
        List<DataVo> dataVos = statisticsService.getActivityData();
        return Result.success(dataVos);
    }

    @GetMapping("line")
    public Result line(){
        LineVo dataVos = statisticsService.getLine();
        return Result.success(dataVos);
    }

    @GetMapping("radar")
    public Result  radar(){
        RadarVo radarVo = statisticsService.getRadar();
        return Result.success(radarVo);
    }

    @GetMapping("collegePoints")
    public Result collegePoints(){
        List<CollegePointsVo> collegePoints = statisticsService.getCollegePoints();
        return Result.success(collegePoints);
    }

}
