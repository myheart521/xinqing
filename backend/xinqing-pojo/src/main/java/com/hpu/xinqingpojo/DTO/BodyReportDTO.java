package com.hpu.xinqingpojo.DTO;

import lombok.Data;

import java.time.LocalDate;

@Data
public class BodyReportDTO {

    private Float height;

    private Float weight;

    private Float bmi;

    private LocalDate createTime;

    private Float tiZhiLv;

    private String equipId;
}
