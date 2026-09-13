package com.hpu.xinqingpojo.entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Doctor {
  private long id;
  private long userId;
  private String background;
  private String doctorName;
  private String title;
  private long pccId;
  private String gender;
  private long rating;
  private String specializedFields;
  private String otherInformation;
  private String photo;
}
