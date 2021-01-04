package com.wakedata.whx.datastream.window;

import com.wakedata.whx.EnvironmentUtil;
import com.wakedata.whx.datastream.util.StringToLongTimeUtil;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.AssignerWithPunctuatedWatermarks;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.apache.flink.streaming.api.functions.windowing.WindowFunction;
import org.apache.flink.streaming.api.watermark.Watermark;
import org.apache.flink.streaming.api.windowing.assigners.SlidingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.streaming.runtime.operators.util.AssignerWithPunctuatedWatermarksAdapter;
import org.apache.flink.util.Collector;

/**
 * @author :wanghuxiong
 * @title: SlideWindow
 * @projectName flink-study
 * @description: TODO
 * @date 2020/11/15 3:36 下午
 */
public class SlideWindowTest {

    public static void main(String[] args) {
        StreamExecutionEnvironment streamExecutionEnvironment = EnvironmentUtil.getStreamExecutionEnvironment();
        streamExecutionEnvironment.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
        streamExecutionEnvironment.setParallelism(3);
        DataStreamSource<Tuple3<String, String, Long>> tuple3DataStreamSource = streamExecutionEnvironment
                .fromCollection(EnvironmentUtil.tuple3StringStringLongElements);

        tuple3DataStreamSource.assignTimestampsAndWatermarks(new AssignerWithPunctuatedWatermarksAdapter.Strategy<Tuple3<String, String, Long>>(
                new AssignerWithPunctuatedWatermarks<Tuple3<String, String, Long>>() {
                    private long maxTimestamp = 0L;
                    // 延迟毫秒数
                    private long delay = 10000L;

                    @Override
                    public Watermark checkAndGetNextWatermark(Tuple3<String, String, Long> stringStringLongTuple3, long l) {
                        if (l > maxTimestamp - delay) {
                            return new Watermark(l - delay);
                        }
                        return null;
                    }

                    @Override
                    public long extractTimestamp(Tuple3<String, String, Long> stringStringLongTuple3, long l) {
                        Long timeStampFromDateString = StringToLongTimeUtil
                                .getTimeStampFromDateString(stringStringLongTuple3.f1);
                        if (timeStampFromDateString > maxTimestamp) {
                            maxTimestamp = timeStampFromDateString;
                        }
                        return timeStampFromDateString;
                    }
                }
        )).keyBy((tuple3) -> {
            return tuple3.f0;
        }).window(SlidingEventTimeWindows.of(Time.seconds(60),Time.seconds(30))).reduce(new ReduceFunction<Tuple3<String, String, Long>>() {
            @Override
            public Tuple3<String, String, Long> reduce(Tuple3<String, String, Long> value1, Tuple3<String, String, Long> value2) throws Exception {
                System.err.println("window中");
                return Tuple3.of(value1.f0, value2.f1, value1.f2 + value2.f2);
            }
        }, new WindowFunction<Tuple3<String, String, Long>, Tuple3<String, String, Long>, String, TimeWindow>() {
            @Override
            public void apply(String s, TimeWindow timeWindow, Iterable<Tuple3<String, String, Long>> iterable, Collector<Tuple3<String, String, Long>> collector) throws Exception {
                iterable.forEach(
                        value -> {
                            System.err.println("获得值：" + value);
                            collector.collect(value);
                        }
                );
            }
        }).addSink(new SinkFunction<Tuple3<String, String, Long>>() {
            @Override
            public void invoke(Tuple3<String, String, Long> value, Context context) throws Exception {
                System.err.println("输出：" + value);
            }
        });

        try {
            streamExecutionEnvironment.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}













