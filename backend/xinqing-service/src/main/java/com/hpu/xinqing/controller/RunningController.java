package com.hpu.xinqing.controller;


import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hpu.xinqing.service.IRunningService;
import com.hpu.xinqingcommon.constant.MessageConstant;
import com.hpu.xinqingcommon.exception.BaseException;
import com.hpu.xinqingcommon.result.Result;
import com.hpu.xinqingpojo.VO.RunningReportVO;
import com.hpu.xinqingpojo.entity.Running;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @since 2024-10-23
 */
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/running")
public class RunningController {
    private final IRunningService runningService;

    @GetMapping("/select")
    public Result<Running> select(){
        Long id= Long.parseLong(StpUtil.getLoginId().toString());
        log.info("查询跑步数据,用户id为：{}",id);
        String equipId=StpUtil.getSession().get("running").toString();
        if(equipId==null||equipId.length()==0){
            throw new BaseException(MessageConstant.EQUIPMENT_NOT_CREATE);
        }
        Running running=runningService.lambdaQuery()
                .eq(Running::getEquipId,equipId)
                .orderByDesc(Running::getCreateTime)
                .list().get(0);
        return Result.success(running);
    }

    @GetMapping("/update")
    public Result<Running> update(){
        Long userId= Long.parseLong(StpUtil.getLoginId().toString());
        log.info("查询更新跑步数据,用户id为：{}",userId);
        Running running=runningService.selectLoTDA(userId);
        return Result.success(running);
    }

    @GetMapping("/dataReport")
    public Result<RunningReportVO> dataReport(
            @DateTimeFormat(pattern = "yyyy-MM-dd")
            LocalDate begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd")
            LocalDate end
    ){
        RunningReportVO runningReportVO=runningService.dataReport(begin,end);
        return Result.success(runningReportVO);
    }


}
