package com.hpu.xinqing.utils.common;

public class StringUtils {

    //判断是否有#
    public static boolean hasSharp(String str){
        return hasSpecialSign(str, "#");
    }

    //判断是否有特殊符号
    public static boolean hasSpecialSign(String str,String sign){
        char[] charArray = str.toCharArray();
        for (char c : charArray) {
            if (c==sign.charAt(0)){
                return true;
            }
        }
        return false;
    }
}
