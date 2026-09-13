package com.hpu.xinqingpojo.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class Sport {

  private Long id;
  private String text;
  private String video;
  private Integer time;
  //卡路里
  private String cal;
  private String image;
  //表示等级, 对应league表的id
  private Long leagueId;
  private String title;
  //运动类型
  private String motion;

}
