package com.wakedata.whx.client.apply;

/**
 * @author :wanghuxiong
 * @title: FourAruFunction
 * @projectName flink-study
 * @description: TODO
 * @date 2020/10/21 12:08 上午
 */
@FunctionalInterface
public interface FourAruFunction<A,B,C,D,R> {

    R apply(A a,B b,C c,D d);
}
