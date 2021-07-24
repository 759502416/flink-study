package com.wakedata.whx.locksupport;

import org.junit.Test;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.LockSupport;

/**
 * @author wanghx
 * @describe
 * @since 2021/7/6 14:05
 */
public class ParkUnparkTest {

    @Test
    public void test1() throws InterruptedException {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                System.err.println("child thread begin park!");
                // 调用park方法，挂起自己
                LockSupport.park();
                System.err.println("child thread unpark!");
            }
        });
        // 启动子线程
        thread.start();
        // 主线程休眠1s
        Thread.sleep(1000);
        System.err.println("main thread begin unpark!");
        // 调用unpark方法让thread线程持有许可证，然后park方法返回
        LockSupport.unpark(thread);
    }

    @Test
    public void test2() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                System.err.println("child thread begin park!");
                // 调用park方法，挂起自己，只有被中断才会退出循环
                while (!Thread.currentThread().isInterrupted()) {
                    LockSupport.park();
                }
                System.err.println("child thread unpark!");
            }
        });
        // 启动子线程
        thread.start();
        // 主线程休眠1秒
        System.err.println("main thread begin unpark!");
        // 中断子线程
        thread.interrupt();
    }

    @Test
    public void test3() throws InterruptedException {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                System.err.println("child thread begin park!");
                LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(8));
                System.err.println("child thread unpark!");
            }
        });
        // 启动子线程
        thread.start();
        Thread.sleep(10000L);
    }
}
