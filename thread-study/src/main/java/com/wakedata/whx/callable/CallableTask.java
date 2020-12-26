package com.wakedata.whx.callable;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * @author :wanghuxiong
 * @title: CallableTask
 * @projectName flink-study
 * @description: TODO
 * @date 2020/12/17 11:58 下午
 */
public class CallableTask implements Callable<String> {
    @Override
    public String call() throws Exception {
        Thread.sleep(3000);
        return "call you";
    }

    public static void main(String[] args) {
        FutureTask<String> stringFutureTask = new FutureTask<>(new CallableTask());
        new Thread(stringFutureTask).start();
        try {
            String s = stringFutureTask.get();
            System.err.println(s);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
}
