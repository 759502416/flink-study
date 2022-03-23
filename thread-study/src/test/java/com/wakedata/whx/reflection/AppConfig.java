package com.wakedata.whx.reflection;

import lombok.Builder;
import lombok.Getter;
import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * @author :wanghuxiong
 * @title: AppConfig
 * @projectName flink-study
 * @description: TODO
 * @date 2021/8/2 9:28 下午
 */
@Builder
@Getter
public class AppConfig {


    private String name;

    private String age;

    public Integer id;

    private Unsafe unsafe;

    public AppConfig(String name, String age, Integer id, Unsafe unsafe) {
        this.name = name;
        this.age = age;
        this.id = id;
        this.unsafe = unsafe;
    }

    public AppConfig() {
        try {
            Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            unsafe = (Unsafe)theUnsafe.get(null);

        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
