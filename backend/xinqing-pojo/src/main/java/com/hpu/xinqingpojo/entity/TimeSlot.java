package com.hpu.xinqingpojo.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TimeSlot {

  private long id;
  private String timeDesc;
  private String startTime;
  private String endTime;
  private long pccId;
  private String approximateTime;
}
