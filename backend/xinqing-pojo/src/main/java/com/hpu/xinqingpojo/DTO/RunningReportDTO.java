package com.hpu.xinqingpojo.DTO;

import lombok.Data;

import java.time.LocalDate;

@Data
public class RunningReportDTO {
    private LocalDate createTime;
    //每日总跑步时间，以逗号分隔，例如：260,210,215
    private Float longTime;

    //每日跑步总距离，以逗号分隔，例如：20,21,10
    private Float distance;
}
