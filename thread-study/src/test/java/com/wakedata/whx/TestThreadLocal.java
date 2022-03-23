package com.wakedata.whx;

import org.junit.Test;

/**
 * @author wanghx
 * @describe
 * @since 2022/3/11 10:28
 */
public class TestThreadLocal {
    private static ThreadLocal<StringBuffer> stringBufferThreadLocal = new ThreadLocal<>();
    @Test
    public void initThreadLocal() throws InterruptedException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.err.println(stringBufferThreadLocal.get() == null);
                stringBufferThreadLocal.set(new StringBuffer("a"));
                System.err.println(stringBufferThreadLocal.get() == null);
            }
        }).start();
        Thread.sleep(3000);
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.err.println(stringBufferThreadLocal.get() == null);
            }
        }).start();
        Thread.sleep(3000);
    }
}
