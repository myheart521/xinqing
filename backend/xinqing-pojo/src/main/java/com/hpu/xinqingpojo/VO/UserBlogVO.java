package com.hpu.xinqingpojo.VO;

import lombok.Data;

import java.util.List;


@Data
public class UserBlogVO {

    private List<String> avatar;
    private String username;
    private String desc;

}
