package com.wakedata.whx;

import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.ArrayList;
import java.util.List;

/**
 * @author :wanghuxiong
 * @title: EnvironmentUtil
 * @projectName flink-study
 * @description: TODO
 * @date 2020/11/8 3:19 下午
 */
public class EnvironmentUtil {

    private volatile static StreamExecutionEnvironment streamExecutionEnvironment;

    public static final ArrayList<Tuple3<String, String, Long>> tuple3StringStringLongElements = new ArrayList<>();

    static {
        Tuple3<String, String, Long> tuple0 = Tuple3.of("w0", "2020-12-01 11:07:11", 3L);
        Tuple3<String, String, Long> tuple00 = Tuple3.of("w0", "2020-12-01 11:07:12", 3L);
        Tuple3<String, String, Long> tuple1 = Tuple3.of("w1", "2020-12-01 11:11:09", 3L);
        Tuple3<String, String, Long> tuple2 = Tuple3.of("w2", "2020-12-01 11:11:13", 6L);
        Tuple3<String, String, Long> tuple3 = Tuple3.of("w3", "2020-12-01 11:12:03", 7L);
        Tuple3<String, String, Long> tuple4 = Tuple3.of("w1", "2020-12-01 11:12:09", 8L);
        Tuple3<String, String, Long> tuple5 = Tuple3.of("w2", "2020-12-01 11:12:11", 10L);//*
        Tuple3<String, String, Long> tuple6 = Tuple3.of("w3", "2020-12-01 11:12:13", 13L);//*
        Tuple3<String, String, Long> tuple7 = Tuple3.of("w1", "2020-12-01 11:13:14", 15L);//*
        Tuple3<String, String, Long> tuple8 = Tuple3.of("w2", "2020-12-01 11:13:16", 17L);//*
        Tuple3<String, String, Long> tuple9 = Tuple3.of("w3", "2020-12-01 11:13:17", 20L);//*
        Tuple3<String, String, Long> tuple10 = Tuple3.of("w1", "2020-12-01 11:14:18", 21L);//*
        Tuple3<String, String, Long> tuple11 = Tuple3.of("w2", "2020-12-01 11:14:12", 25L);//*
        Tuple3<String, String, Long> tuple12 = Tuple3.of("w3", "2020-12-01 11:14:14", 27L);//*
        quickAddElementToList(tuple3StringStringLongElements,
                tuple0,
                tuple00,
                tuple1,
                tuple2,
                tuple3,
                tuple4,
                tuple5,
                tuple6,
                tuple7,
                tuple8,
                tuple9,
                tuple10,
                tuple11,
                tuple12
        );
    }

    /**
     * 创建执行环境
     * @return
     */
    public static StreamExecutionEnvironment getStreamExecutionEnvironment() {
        if (streamExecutionEnvironment == null) {
            synchronized (EnvironmentUtil.class) {
                if (streamExecutionEnvironment == null) {
                    return StreamExecutionEnvironment.getExecutionEnvironment();
                }
            }
        }
        return streamExecutionEnvironment;
    }

    /**
     * 快速插入元素到list中
     *
     * @param list
     * @param element
     */
    public static void quickAddElementToList(List list, Object... element) {
        for (int i = 0; i < element.length; i++) {
            list.add(element[i]);
        }
    }
}
