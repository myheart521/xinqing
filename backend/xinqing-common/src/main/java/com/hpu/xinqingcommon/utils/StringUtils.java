package com.hpu.xinqingcommon.utils;

import java.util.Arrays;
import java.util.List;

public class StringUtils {
    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    public static List<String> imageToList(String images) {
        return Arrays.asList(images.split(","));

    }
    public static String listToImage(List<String> imageList) {
        return String.join(",", imageList);
    }
}
