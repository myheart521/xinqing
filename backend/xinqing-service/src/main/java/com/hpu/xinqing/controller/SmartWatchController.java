package com.hpu.xinqing.controller;

import com.hpu.xinqing.service.SmartWatchService;
import com.hpu.xinqingcommon.result.Result;
import com.hpu.xinqingpojo.VO.SmartWatchVO;
import com.hpu.xinqingpojo.VO.SmartwatchReportVO;
import com.hpu.xinqingpojo.enmus.DeviceType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@CrossOrigin
@RestController
@Slf4j
@RequestMapping("/smartWatch")
public class SmartWatchController {

    @Autowired
    private SmartWatchService smartWatchService;
    @GetMapping("/smartWatch")
    public Result<SmartWatchVO> getSmartWatch(){
        DeviceType deviceType = DeviceType.smartWatch;
         SmartWatchVO smartWatchData = smartWatchService.updateSmartWatchData(deviceType);
        return Result.success(smartWatchData);
    }
//    @GetMapping("/create/{deviceType}")
//    public Result createSmartWatch(@PathVariable DeviceType deviceType){
//        smartWatchService.createDevice(deviceType);
//        return Result.success();
//    }
    @GetMapping("/getNewDate/{deviceType}")
    public Result<SmartWatchVO> getNewDate(@PathVariable DeviceType deviceType){
        SmartWatchVO newData = smartWatchService.getSmartWatchData(deviceType);
        return Result.success(newData);
    }
    @GetMapping("/data")
    public Result<SmartwatchReportVO> dataReport(
            @DateTimeFormat(pattern = "yyyy-MM-dd")
            LocalDate begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd")
            LocalDate end
    ){
        log.info("身体数据统计，日期范围：{}~{}",begin,end);
        SmartwatchReportVO smartwatchReportVO= smartWatchService.dataReport(begin,end);
        return Result.success(smartwatchReportVO);
    }
}
