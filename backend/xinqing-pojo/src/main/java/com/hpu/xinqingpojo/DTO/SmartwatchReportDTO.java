package com.hpu.xinqingpojo.DTO;

import lombok.Data;

import java.time.LocalDate;

@Data
public class SmartwatchReportDTO {
    private LocalDate createTime;
    //睡眠时间
    private Float sleepTime;
    //久坐时间
    private Float sedentaryTime;
    //心率
    private Float heartRate;
    //体温
    private Float bodyTemperature;

}
