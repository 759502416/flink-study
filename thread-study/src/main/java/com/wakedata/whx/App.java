package com.wakedata.whx;

/**
 * Hello world!
 */
public class App {
    public  static  String a ="a";

    public static void main(String[] args) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                synchronized (a) {
                    try {
                        System.err.println("nadao");
                        Thread.sleep(5000L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.err.println(a);
                }
            }
        };
        new Thread(runnable).start();
        try {
            Thread.sleep(2000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        synchronized (a) {
            System.err.println(a+"1");
        }


    }
}
