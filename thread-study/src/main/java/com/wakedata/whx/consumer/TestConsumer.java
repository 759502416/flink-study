package com.wakedata.whx.consumer;

import java.util.function.Consumer;

/**
 * @author wanghx
 * @describe
 * @since 2021/9/12 14:28
 */
public class TestConsumer {

    public static void main(String[] args) {
        testConsumer();
        testAndThen();
    }
    /**
     * 一个简单的平方计算
     */
    public static void testConsumer() {
        //设置好Consumer实现方法
        Consumer<Integer> square = x -> System.out.println("平方计算 : " + x * x);
        //传入值
        square.accept(2);
    }
    /**
     * 定义3个Consumer并按顺序进行调用andThen方法
     */
    public static void testAndThen() {
        //当前值
        Consumer<Integer> consumer1 = x -> System.out.println("当前值 : " + x);
        //相加
        Consumer<Integer> consumer2 = x -> { System.out.println("相加 : " + (x + x)); };
        //相乘
        Consumer<Integer> consumer3 = x -> System.out.println("相乘 : " + x * x);
        //andThen拼接
        consumer1.andThen(consumer2).andThen(consumer3).accept(1);
    }
}
