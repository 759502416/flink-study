package com.wakedata.whx.classloader;


/**
 * Thread线程API中，getContextClassLoader()方法的描述为：
 * 返回该线程的ClassLoader上下文。线程创建者提供ClassLoader上下文，以便运行在该线程的代码在加载类和资源时使用。如果没有，则默认返回父线程的ClassLoader上下文。原始线程的上下文 ClassLoader 通常设定为用于加载应用程序的类加载器。
 *
 * 首先，如果有安全管理器，并且调用者的类加载器不是 null，也不同于其上下文类加载器正在被请求的线程上下文类加载器的祖先，则通过 RuntimePermission("getClassLoader") 权限调用该安全管理器的 checkPermission 方法，查看是否可以获取上下文 ClassLoader。
 *
 * @author :wanghuxiong
 * @title: ContextClassLoaderTest
 * @projectName flink-study
 * @description: TODO
 * @date 2020/10/14 11:52 下午
 */
public class ContextClassLoaderTest {

    public static void main(String[] args) {
        System.err.println(Thread.currentThread().getId() + "-outer:" + Thread.currentThread().getContextClassLoader());
        System.err.println(Thread.currentThread().getId() + "-outer:" + String.class.getClassLoader());
        System.err.println(Thread.currentThread().getId() + "-outer:" + ContextClassLoaderTest.class.getClassLoader());
        new Thread() {
            @Override
            public void run() {
                System.err.println(Thread.currentThread().getId() + "-outer:" + Thread.currentThread().getContextClassLoader());
            }
        }.start();

        new Thread() {
            @Override
            public void run() {
                System.err.println(Thread.currentThread().getId() + "-outer:" + Thread.currentThread().getContextClassLoader());
            }
        }.start();
        // 输出结果为
        //        1-outer:sun.misc.Launcher$AppClassLoader@18b4aac2
        //        11-outer:sun.misc.Launcher$AppClassLoader@18b4aac2
        //        12-outer:sun.misc.Launcher$AppClassLoader@18b4aac2
        // 从上可以看出  线程默认的类加载器就是AppClassLoader
        

    }
}
