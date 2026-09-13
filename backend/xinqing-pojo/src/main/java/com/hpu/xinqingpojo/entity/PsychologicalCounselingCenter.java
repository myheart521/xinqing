package com.hpu.xinqingpojo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PsychologicalCounselingCenter {

  private long id;
  private String phoneNumber;
  private String email;
  private String location;
  private String publicAccount;
  private String method;
  private String school;
  private String description;
  private String province;
  private String src;

}
