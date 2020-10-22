package com.wakedata.whx.client.apply;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * @author :wanghuxiong
 * @title: TestFourAruFunction
 * @projectName flink-study
 * @description: TODO
 * @date 2020/10/21 12:10 上午
 */
public class TestFourAruFunction {

    public static FourAruFunction<String, String, String, String, CompletableFuture<String>> futureFourAruFunction =
            (a, b, c, d) -> CompletableFuture.completedFuture(a + b + c + d);

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        System.err.println(futureFourAruFunction.apply("I","L","Y","C").get());
    }
}
