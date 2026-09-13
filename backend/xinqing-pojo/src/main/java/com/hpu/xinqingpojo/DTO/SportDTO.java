package com.hpu.xinqingpojo.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SportDTO {
    private String title;
    private String league;
    private Integer startTime;
    private Integer endTime;
    private Long leagueId;

}

