package com.hpu.xinqingpojo.VO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 大致信息
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SportVO {
    private Long id;
    private Integer time;
    private String image;
    private String title;
    private String league;
    private String leagueDescribe;
    private String motion;
    private List<String> motionFeatures;

}
