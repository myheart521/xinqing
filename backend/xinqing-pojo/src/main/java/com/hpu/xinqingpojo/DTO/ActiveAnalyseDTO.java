package com.hpu.xinqingpojo.DTO;

import dev.langchain4j.model.output.structured.Description;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Description("活动分析")
public class ActiveAnalyseDTO {
    //活动的id
    @Description("活动id")
    private Long id;

    //活动的内容
    @Description("活动内容")
    private String content;

    //活动的标题
    @Description("活动标题")
    private String title;

    //活动的标签
    @Description("活动标签")
    private String tag;

    //活动的地址
    @Description("活动地址")
    private String address;

    //活动的时间
    @Description("活动时间")
    private LocalDateTime time;
}
