package com.wakedata.whx.reflection;

/**
 * @author :wanghuxiong
 * @title: UnsafeTest
 * @projectName flink-study
 * @description: TODO
 * @date 2021/8/2 10:13 下午
 */
public class UnsafeTest {
    public static void main(String[] args) {
        AppConfig whx = AppConfig.builder()
                .id(1)
                .name("whx")
                .age("18")
                .build();

    }
}
