package com.hpu.xinqingpojo.VO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TestVO {
    private String relationship;
    private String study;
    private String campus;
    private String job;
    private String emotion;
    private String self;
    private String satisfaction;
    private String total;
    private String[] good;
    private String[] bad;
    private String judge;

    @Override
    public String toString() {
        return "TestReturn{" +
                "relationship='" + relationship + '\'' +
                ", study='" + study + '\'' +
                ", campus='" + campus + '\'' +
                ", job='" + job + '\'' +
                ", emotion='" + emotion + '\'' +
                ", self='" + self + '\'' +
                ", satisfaction='" + satisfaction + '\'' +
                ", total='" + total + '\'' +
                ", good=" + Arrays.toString(good) +
                ", bad=" + Arrays.toString(bad) +
                ", judge='" + judge + '\'' +
                '}';
    }
}
