package com.hpu.xinqingpojo.VO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RunningReportVO {
    //日期，以逗号分隔，例如：2022-10-01,2022-10-02,2022-10-03
    private String dateList;

    //每日总跑步时间，以逗号分隔，例如：260,210,215
    private String longTimeList;

    //每日跑步总距离，以逗号分隔，例如：20,21,10
    private String distanceCountList;

}
