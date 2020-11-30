package com.wakedata.whx.sink;

import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;

/**
 * TODO
 *
 * @author: WangHuXiong
 * @title: MyTestCheckPointSink
 * @date: 2020-11-27 16:22
 * @program: FlinkStudy
 * @description:
 **/
public class MyTestCheckPointSink extends RichSinkFunction<String>{
    public Integer time = 0;

    @Override
    public void invoke(String value, Context context) throws Exception {
        Integer integer1 = Integer.valueOf(value);
        if (integer1 >79) {
            System.err.println("抛出异常");
            throw new Exception("bye");
        }
        System.err.println("线程为："+Thread.currentThread().getName()+"，time为:"+time+",得到结果为:" + value);
        time ++;
    }
}
