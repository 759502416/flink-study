package com.wakedata.whx.join;

/**
 * @author :wanghuxiong
 * @title: ThreadJoinTest
 * @projectName flink-study
 * @description: TODO
 * @date 2020/12/28 11:27 下午
 */
public class ThreadJoinTest {
    public static void main(String[] args) {
        Thread thread1 = new Thread(() -> {
            System.err.println("threadOne begin run!");
            for (; ; ) {
            }
        });
       final  Thread mainThread = Thread.currentThread();

        Thread thread2 = new Thread(() -> {
            System.err.println("休眠一秒");
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // 中断主线程
            mainThread.interrupt();
        });

        thread1.start();
        thread2.start();
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            thread2.join();
        } catch (Exception e) {
            System.err.println("主线程发生异常");
            e.printStackTrace();
        }
    }
}
