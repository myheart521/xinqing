package com.hpu.xinqingpojo.VO;

import lombok.Data;

@Data
public class BlogMessageVO {

    //获赞总数
    private Long likeCount;
    //热评总数
    private Long hotReviewsCount;
    //粉丝数
    private Long fansCount;
    //关注其他的数量
    private Long focusCount;
}
