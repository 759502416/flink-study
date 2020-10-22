package com.wakedata.whx.classloader;

/**
 * @author :wanghuxiong
 * @title: MuClassLoader
 * @projectName flink-study
 * @description: TODO
 * @date 2020/10/15 10:53 下午
 */
public class MuClassLoader extends ClassLoader {
    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        return super.loadClass(name, resolve);
    }
}
