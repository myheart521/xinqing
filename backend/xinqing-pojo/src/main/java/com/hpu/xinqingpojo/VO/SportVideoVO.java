package com.hpu.xinqingpojo.VO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 具体信息
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SportVideoVO {
    private Long id;
    private String text;
    private String video;
    private Integer time;
    private String cal;
    private List<String> imageList;
    private String title;
    private String league;
    private String leagueDescribe;
    private String motion;
    private List<String> motionFeatures;
}
