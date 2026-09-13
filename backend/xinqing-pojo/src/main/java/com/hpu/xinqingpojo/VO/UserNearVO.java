package com.hpu.xinqingpojo.VO;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true)
public class UserNearVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    /**
     * 用户昵称
     */
    private String name;

    /**
     * 用户头像
     */
    private String url;

    /**
     * 用户简介
     */
    private String desc;

    private String sex;

    //维度
    private Double latitude;
    //经度
    private Double longitude;
    //距离
    private Double distance;

}
