package com.wakedata.whx.locksupport;

import java.util.concurrent.locks.LockSupport;

/**
 * @author wanghx
 * @describe
 * @since 2021/6/28 14:49
 */
public class LockSupportUtil {

    public static void main(String[] args) {
        System.err.println("begin park!");
        // 使当前线程获取到许可证
        LockSupport.unpark(Thread.currentThread());
        // 再次调用park方法
        LockSupport.park();
        System.err.println("end park!");
    }
}
