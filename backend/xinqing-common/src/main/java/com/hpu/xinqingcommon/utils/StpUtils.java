package com.hpu.xinqingcommon.utils;

import cn.dev33.satoken.stp.StpUtil;

public class StpUtils {

    public static Long userId(){
        long userId;
        try {
            userId = Long.parseLong(StpUtil.getLoginId().toString());
        } catch (Exception e) {
            throw new RuntimeException("用户未登录");
        }
       return userId;
    }



    public static Long userId(String token){
        return Long.parseLong((String) StpUtil.getLoginIdByToken(token));
    }
}
