package com.wakedata.whx.interrupt;

/**
 * @author :wanghuxiong
 * @title: InterruptTest
 * @projectName flink-study
 * @description: TODO
 * @date 2020/12/29 10:51 下午
 */
public class InterruptTest {

    public static void main(String[] args) {
        Thread thread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                System.out.println(Thread.currentThread() + " hello ");
                try {
                    Thread.sleep(20000);
                } catch (InterruptedException e) {

                }
            }
        });
        // 启动子线程
        thread.start();
        // 主线程休眠1S,以便中断前让子线程输出
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        thread.interrupt();
       // thread.isInterrupted();
        try {
            thread.join();
            System.out.println("main is over");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
