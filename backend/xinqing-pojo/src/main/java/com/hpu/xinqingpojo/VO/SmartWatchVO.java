package com.hpu.xinqingpojo.VO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SmartWatchVO {
    String sleepTime;
    String sedentaryTime;
    String heartRate;
    String bodyTemperature;
}
