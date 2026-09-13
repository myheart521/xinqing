package com.hpu.xinqing.utils.common;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.*;

public class ObjToObj {


    //类中对象全部属性转为map
    public static <T> Map<String, Object> objectToMap(T object) {
        HashMap<String, Object> result = new HashMap<>();

        Class<?> clazz = object.getClass();
        Field[] fields = getAllFields(clazz); // 获取当前类及父类的字段
        for (Field item : fields) {
            item.setAccessible(true);
            try {
                Object value = item.get(object); // 获取字段值
                if (value != null) {
                    if (value instanceof Date) {
                        result.put(item.getName(), new SimpleDateFormat("yyyy-MM-dd").format(value));
                    } else {
                        result.put(item.getName(), value.toString());
                    }
                } else {
                    result.put(item.getName(), null); // 如果需要保留 null 值
                }
            } catch (IllegalAccessException e) {
                System.out.println("Error Occurred: " + e);
                throw new RuntimeException(e);
            }
        }
        return result;
    }

    private static Field[] getAllFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        while (clazz != null) {
            fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
            clazz = clazz.getSuperclass();
        }
        return fields.toArray(new Field[0]);
    }
}