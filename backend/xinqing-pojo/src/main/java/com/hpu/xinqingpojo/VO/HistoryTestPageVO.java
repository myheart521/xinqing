package com.hpu.xinqingpojo.VO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistoryTestPageVO {

    private Long id;

    private String title;

    private String src;


    private LocalDateTime createTime;

}
