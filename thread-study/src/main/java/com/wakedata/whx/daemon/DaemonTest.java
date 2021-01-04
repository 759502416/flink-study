package com.wakedata.whx.daemon;

/**
 * @author :wanghuxiong
 * @title: DaemonTest
 * @projectName flink-study
 * @description: TODO
 * @date 2020/12/29 11:13 下午
 */
public class DaemonTest {
    public static void main(String[] args) {
        Thread thread = new Thread(() -> {
            try {
                Thread.sleep(30000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        thread.setDaemon(true);
        thread.start();

    }
}
