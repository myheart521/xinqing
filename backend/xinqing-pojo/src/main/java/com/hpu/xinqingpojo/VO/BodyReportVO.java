package com.hpu.xinqingpojo.VO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BodyReportVO {
    //日期，以逗号分隔，例如：2022-10-01,2022-10-02,2022-10-03
    private String dateList;

    //每日身高，以逗号分隔，例如：260,210,215
    private String heightList;

    //每日体重，以逗号分隔，例如：20,21,10
    private String weightList;

    //每日BMI
    private String bmiList;

    //体脂率
    private String tiZhiLvList;

}
