package com.hpu.xinqingpojo.VO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class CollegeCountVo {
    String name;
    Integer count;
    Double latitude;
    Double longitude;
}
