package com.hpu.xinqingpojo.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SportImage {

  private Long id;
  private String image;
  private Long sportId;

}
