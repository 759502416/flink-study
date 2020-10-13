package com.wakedata.whx.stacktrace;

import org.junit.Test;

/**
 * @author :wanghuxiong
 * @title: StackTraceTest
 * @projectName flink-study
 * @description: TODO
 * @date 2020/10/13 10:50 下午
 */

public class StackTraceTest {

    @Test
    public void testStackTrace() {
        testFunctionA();
    }

    public static void testFunctionA() {
        testFunctionB();
    }

    public static void testFunctionB() {
        System.out.println("B");
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        for (StackTraceElement stackTraceElement : stackTrace) {
            System.err.println(stackTraceElement.toString());
            System.err.println("----");
        }
    }
}
