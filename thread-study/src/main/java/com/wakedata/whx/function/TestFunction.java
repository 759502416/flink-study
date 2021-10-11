package com.wakedata.whx.function;

import java.util.function.Function;

/**
 * @author wanghx
 * @describe
 * @since 2021/9/12 14:50
 */
public class TestFunction {

    private static Integer num = 1;

    private static void applyTest() {
        Function<String, Integer> function = new Function<String, Integer>() {
            @Override
            public Integer apply(String s) {
                num = Integer.parseInt(s) + num;
                return num;
            }
        };
        Integer a = function.apply("100");
        System.err.println(a);
    }

    private static void applyTest2() {
        Function<String, Integer> function = new Function<String, Integer>() {
            @Override
            public Integer apply(String s) {
                num = Integer.parseInt(s) + num;
                return num;
            }
        };
        Integer a = function.apply("10");
        System.err.println(a);
    }

    public static void main(String[] args) {
        applyTest();
        applyTest2();
    }
}
