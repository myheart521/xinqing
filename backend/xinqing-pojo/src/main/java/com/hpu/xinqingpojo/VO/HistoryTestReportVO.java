package com.hpu.xinqingpojo.VO;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistoryTestReportVO {
    Long moduleId;
    String title;
    List<LocalDateTime> xAxis;
    HashMap<String, List<Integer>> yAxis;
}
