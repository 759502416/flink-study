package com.wakedata.whx.client.apply;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.BiFunction;

/**
 * @author :wanghuxiong
 * @title: TestApplyMethod
 * @projectName flink-study
 * @description: 测试应用方法
 * @date 2020/10/21 12:04 上午
 */
public class TestApplyMethod {

    private static BiFunction<String, String, CompletableFuture<String>> biFunction = (str1, str2) -> CompletableFuture.completedFuture(str1 + str2);

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        System.err.println(biFunction.apply("whx love","xc").get());
    }
}
