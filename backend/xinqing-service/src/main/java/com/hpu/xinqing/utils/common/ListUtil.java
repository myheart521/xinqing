package com.hpu.xinqing.utils.common;

import java.util.List;

public class ListUtil {
    public static boolean isNotEmpty(List<?> list){
        return (list!=null&&(!list.isEmpty()));
    }
}
