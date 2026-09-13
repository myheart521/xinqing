package com.hpu.xinqingpojo.VO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SmartwatchReportVO {
    //日期，以逗号分隔，例如：2022-10-01,2022-10-02,2022-10-03
    private String dateList;
    //睡眠时间
    private String sleepTimeList;
    //久坐时间
    private String sedentaryTimeList;
    //心率
    private String heartRateList;
    //体温
    private String bodyTemperatureList;

}
