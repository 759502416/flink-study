package com.wakedata.whx.future;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author :wanghuxiong
 * @title: CompleteFutureTest
 * @projectName flink-study
 * @description: TODO
 * @date 2021/7/29 11:03 下午
 */
public class CompleteFutureTest {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ExecutorService executorService = Executors.newCachedThreadPool();
        CompletableFuture<Integer> completableFuture = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(10000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return 1;
        }, executorService);
        Integer integer = completableFuture.get();
        //Preconditions.checkCompletedNormally(completableFuture);
        System.err.println(integer);
    }
}
