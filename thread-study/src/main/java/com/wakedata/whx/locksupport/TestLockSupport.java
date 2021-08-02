package com.wakedata.whx.locksupport;

import java.util.concurrent.locks.LockSupport;

/**
 * @author :wanghuxiong
 * @title: TestLockSupport
 * @projectName flink-study
 * @description: TODO
 * @date 2021/6/17 11:42 下午
 */
public class TestLockSupport {

    public static Object u = new Object();

    static ChangeObjectThread t1 = new ChangeObjectThread("t1");

    static ChangeObjectThread t2 = new ChangeObjectThread("t2");

    public static class ChangeObjectThread extends Thread {

        public ChangeObjectThread(String name) {
            super(name);
        }

        @Override
        public void run() {
            synchronized (u) {
                System.err.println("in " + getName());
                LockSupport.park();
                if (Thread.currentThread().isInterrupted()) {
                    System.err.println("被中断了");
                }
                System.err.println("继续执行");
            }
        }
    }


    public static void main(String[] args) throws InterruptedException {
        t1.start();
        Thread.sleep(1000L);
        t2.start();
        Thread.sleep(1000L);
        t1.interrupt();
        LockSupport.unpark(t2);
        t1.join();
        t2.join();
        System.err.println("over");
    }
}
