package com.wakedata.whx.reflection;

import java.lang.reflect.Field;

/**
 * @author :wanghuxiong
 * @title: TestDeclaredField
 * @projectName flink-study
 * @description: TODO
 * @date 2021/8/2 9:27 下午
 */
public class TestDeclaredField {

    public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException {
        Field name = AppConfig.class.getDeclaredField("name");
        name.setAccessible(true);
        AppConfig whx = AppConfig.builder()
                .age("18")
                .id(1)
                .name("whx")
                .build();
        Object o = name.get(null);
        System.err.println(o);
    }
}
