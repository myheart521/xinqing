package com.hpu.xinqingpojo.VO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserCount {
    //用户参与活动数量
    private Long userCount;

    //用户发布动态数量
    private Long dynamicCount;

    //用户测评数量
    private Long evaluateCount;
}
