package com.wakedata.whx.wait;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

/**
 * @author :wanghuxiong
 * @title: ConsumerProducer
 * @projectName flink-study
 * @description: 消费者生产者
 * @date 2020/12/24 12:17 上午
 */
public class ConsumerProducer {

    public static volatile ArrayBlockingQueue<String> queue = new ArrayBlockingQueue<String>(10);

    public static String a = "a";

    public static void main(String[] args) {
        FutureTask<String> stringFutureTask = new FutureTask<String>(new Callable<String>() {
            @Override
            public String call() throws Exception {

                synchronized (queue) {
                    while (a.equals("a")) {
                       // while (queue.size() > 0) {
                        while (queue.size() >= 10) {
                            queue.wait();
                        }
                        System.err.println("生产");
                        queue.add(queue.size() +1+ "");
                        queue.notifyAll();
                    }
                }
                return "a";
            }
        });
        new Thread(stringFutureTask).start();

        FutureTask<String> stringFutureTask2 = new FutureTask<String>(new Callable<String>() {
            @Override
            public String call() throws Exception {

                synchronized (queue) {
                    while (a.equals("a")) {
                        while (queue.size() == 0) {
                            queue.wait();
                        }
                        String take = queue.take();
                        System.err.println("消费：" + take);
                        queue.notifyAll();
                    }
                }
                return "b";
            }
        });
        new Thread(stringFutureTask2).start();
    }
}
