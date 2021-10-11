package com.wakedata.whx.reflection;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * @author :wanghuxiong
 * @title: TestUnsafe
 * @projectName flink-study
 * @description: TODO
 * @date 2021/8/2 9:39 下午
 */
public class TestUnsafe {

    private int count = 0;

    public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException {
        Field unsafeField = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
        unsafeField.setAccessible(true);
        Unsafe unsafe = (Unsafe) unsafeField.get(null);

        Class<AppConfig> appConfigClass = AppConfig.class;
        Field name = appConfigClass.getDeclaredField("name");
        name.setAccessible(true);

        Field age = appConfigClass.getDeclaredField("age");
        age.setAccessible(true);


        long l = unsafe.objectFieldOffset(name);
        long l1 = unsafe.objectFieldOffset(age);
        System.err.println(l);
        System.err.println(l1);

        AppConfig appConfig = AppConfig.builder()
                .name("xc")
                .id(1)
                .age("18")
                .build();
        // CAS替换对象属性
        boolean whx = unsafe.compareAndSwapObject(appConfig, l, appConfig.getName(), "whx");
        System.err.println(appConfig.getName());
    }
}
