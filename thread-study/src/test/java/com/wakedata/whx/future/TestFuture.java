package com.wakedata.whx.future;

import org.apache.flink.util.Preconditions;

import java.util.concurrent.*;
import java.util.function.Predicate;

/**
 * @author :wanghuxiong
 * @title: TestFuture
 * @projectName flink-study
 * @description: TODO
 * @date 2021/7/29 10:04 下午
 */
public class TestFuture {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Callable<String> callable = new Callable<String>() {
            @Override
            public String call() throws Exception {
                Thread.sleep(10000L);
                return "ok";
            }
        };
        ExecutorService executor = Executors.newFixedThreadPool(4);
        Future<String> future = executor.submit(callable);
        String s = future.get();
        System.err.println(s);

    }
}
