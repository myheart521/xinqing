package com.hpu.xinqingcommon.constant;

public class RegexConstant {
    //验证是否为邮箱的正则表达式
    public static final String EMAIL = "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$";
    //验证mbti
    public static final String MBTI = "^[EISNFTJP]{28}$";
    //验证抑郁
    public static final String DEPRESS = "^\\d{20}$";
    //验证自适应测试
    public static final String ADAPTATION = "^\\d{60}$";
    //一定要是一个网址
    public static final String INTERNET = "^((https?|ftp):\\/\\/)?(([a-zA-Z0-9_-]+\\.)*[a-zA-Z0-9_-]+\\.[a-zA-Z]{2,})(\\/[^\\s]*)?$";
    //长度不超过200
    public static final String PROFILE = "^.{1,200}$";
    //密码必须是6-18位
    public static final String PASSWORD = "^\\w{6,18}$";
    //账号必须
    public static final String ACCOUNT = "^[a-zA-Z0-9]{5,12}$";
    //用户名6-12位，不可包含空格
    public static final String USERNAME = "^(?:[^\\s]){6,12}$";
}
