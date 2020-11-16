package com.wakedata.whx.datastream;

import com.wakedata.whx.EnvironmentUtil;
import com.wakedata.whx.datastream.state.CountWindowAverage;
import com.wakedata.whx.datastream.util.StringToLongTimeUtil;
import com.wakedata.whx.datastream.watermark.MyWatermarkStrategy;
import org.apache.flink.api.common.eventtime.*;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.AssignerWithPunctuatedWatermarks;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.apache.flink.streaming.api.watermark.Watermark;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.runtime.operators.util.AssignerWithPeriodicWatermarksAdapter;
import org.apache.flink.streaming.runtime.operators.util.AssignerWithPunctuatedWatermarksAdapter;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author :wanghuxiong
 * @title: AssigningTimestamps
 * @projectName flink-study
 * @description: 提取水位线
 * @date 2020/11/8 3:16 下午
 */
public class AssigningTimestamps {

    public static void main(String[] args) {
        StreamExecutionEnvironment streamExecutionEnvironment = EnvironmentUtil.getStreamExecutionEnvironment();

        streamExecutionEnvironment.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
        streamExecutionEnvironment.setParallelism(3);

        // 获取数据源
        DataStreamSource<Tuple3<String, String, Long>> tuple3DataStreamSource = streamExecutionEnvironment
                .fromCollection(EnvironmentUtil.tuple3StringStringLongElements);

        //
        /*SingleOutputStreamOperator<Tuple3<String, String, Long>> w1 = tuple3DataStreamSource.filter(value -> value.f0.length() >= 0)
                .assignTimestampsAndWatermarks(
                        new MyWatermarkStrategy()1
                );
*/

        SingleOutputStreamOperator<Tuple3<String, String, Long>> w1 = tuple3DataStreamSource.filter(value -> value.f0.length() >= 0)
                .assignTimestampsAndWatermarks(new AssignerWithPunctuatedWatermarksAdapter.Strategy<Tuple3<String, String, Long>>(
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
                        )
                );

        w1.keyBy(new KeySelector<Tuple3<String, String, Long>, Object>() {
            @Override
            public Object getKey(Tuple3<String, String, Long> stringStringLongTuple3) throws Exception {
                return stringStringLongTuple3.f0;
            }
        }).map((new CountWindowAverage()))
                .keyBy(new KeySelector<Tuple3<String, String, Long>, Object>() {
                    @Override
                    public Object getKey(Tuple3<String, String, Long> stringStringLongTuple3) throws Exception {
                        return stringStringLongTuple3.f0;
                    }
                })
                .timeWindow(Time.seconds(120))
                .reduce((v1, v2) -> Tuple3.of(v1.f0, v1.f1, v1.f2 + v2.f2))
                .addSink(new SinkFunction<Tuple3<String, String, Long>>() {
                    @Override
                    public void invoke(Tuple3<String, String, Long> value, Context context) throws Exception {
                        System.err.println("结果是：" + value.toString());
                    }
                });

        try {
            streamExecutionEnvironment.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
