package com.wakedata.whx.wait;

import org.omg.Messaging.SYNC_WITH_TRANSPORT;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

/**
 * @author :wanghuxiong
 * @title: Test
 * @projectName flink-study
 * @description: TODO
 * @date 2020/12/23 11:32 下午
 */
public class Test {
    private static volatile String name = "whx";

    public static void main(String[] args) throws InterruptedException {

        Callable<String> stringCallable = new Callable<String>() {
            @Override
            public String call() throws Exception {
                synchronized (name) {
                    while (name.equals("whx")) {
                        System.err.println("线程A：" + name);
                        name.wait();
                    }
                    name = "wc";
                    System.err.println("线程A走出");
                    return null;
                }
            }
        };

        FutureTask<String> stringFutureTask = new FutureTask<>(stringCallable);
        new Thread(stringFutureTask).start();

        Thread.sleep(1000L);
        synchronized (name) {
            System.err.println("abc");
            name = "wc";
            System.err.println(name);
        }
        System.err.println("wcnmd");
    }
}
