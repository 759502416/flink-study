package com.wakedata.whx;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.apache.flink.api.common.eventtime.TimestampAssigner;
import org.apache.flink.api.common.eventtime.TimestampAssignerSupplier;
import org.apache.flink.api.common.eventtime.Watermark;
import org.apache.flink.api.common.eventtime.WatermarkGenerator;
import org.apache.flink.api.common.eventtime.WatermarkGeneratorSupplier;
import org.apache.flink.api.common.eventtime.WatermarkOutput;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;

/**
 * Hello world!
 */
public class App {

    public static void main(String[] args) throws Exception {
        EnvironmentUtil.turnOnCheckpoint();
        StreamExecutionEnvironment streamExecutionEnvironment = EnvironmentUtil
            .getStreamExecutionEnvironment();
        DataStreamSource<Tuple3<String, String, Long>> tuple3DataStreamSource = streamExecutionEnvironment
            .fromCollection(EnvironmentUtil.tuple3StringStringLongElements);

        SingleOutputStreamOperator<Tuple3<String, String, Long>> tuple3SingleOutputStreamOperator = setWaterMark(
            tuple3DataStreamSource);

        KeyedStream<Tuple3<String, String, Long>, String> tuple3StringKeyedStream =
            tuple3SingleOutputStreamOperator
                .keyBy(new KeySelector<Tuple3<String, String, Long>, String>() {
                    @Override
                    public String getKey(Tuple3<String, String, Long> stringStringLongTuple3)
                        throws Exception {
                        return stringStringLongTuple3.f0;
                    }
                });
        tuple3StringKeyedStream.window(TumblingEventTimeWindows.of(Time.milliseconds(60000L * 3)))
            .reduce(
                new ReduceFunction<Tuple3<String, String, Long>>() {
                    @Override
                    public Tuple3<String, String, Long> reduce(
                        Tuple3<String, String, Long> stringStringLongTuple3,
                        Tuple3<String, String, Long> t1) throws Exception {
                        return Tuple3.of(stringStringLongTuple3.f0, stringStringLongTuple3.f1,
                            stringStringLongTuple3.f2 + t1.f2);
                    }
                }).addSink(new RichSinkFunction<Tuple3<String, String, Long>>() {
            @Override
            public void invoke(Tuple3<String, String, Long> value, Context context)
                throws Exception {
                System.err.println("数据为：" + value.toString());
            }
        });
        streamExecutionEnvironment.execute();
    }

    public static SingleOutputStreamOperator<Tuple3<String, String, Long>> setWaterMark(
        DataStreamSource<Tuple3<String, String, Long>> tuple3DataStreamSource) {
        return tuple3DataStreamSource
            .assignTimestampsAndWatermarks(
                new WatermarkStrategy<Tuple3<String, String, Long>>() {
                    final long maxOutOfOrderness = 1000L;

                    @Override
                    public WatermarkGenerator<Tuple3<String, String, Long>> createWatermarkGenerator(
                        WatermarkGeneratorSupplier.Context context) {
                        return new WatermarkGenerator<Tuple3<String, String, Long>>() {
                            long currentMaxTimestamp = 0L;

                            @Override
                            public void onEvent(Tuple3<String, String, Long> stringStringLongTuple3,
                                long l, WatermarkOutput watermarkOutput) {
                                currentMaxTimestamp = Math
                                    .max(currentMaxTimestamp, l - maxOutOfOrderness);
                                System.err.println("水位线等于：" + currentMaxTimestamp);
                                watermarkOutput.emitWatermark(new Watermark(currentMaxTimestamp));
                            }

                            @Override
                            public void onPeriodicEmit(WatermarkOutput watermarkOutput) {
                                watermarkOutput.emitWatermark(new Watermark(currentMaxTimestamp));
                                System.err.println("周期生成水位线等于：" + currentMaxTimestamp);
                            }

                        };
                    }

                    @Override
                    public TimestampAssigner<Tuple3<String, String, Long>> createTimestampAssigner(
                        TimestampAssignerSupplier.Context context) {
                        return new TimestampAssigner<Tuple3<String, String, Long>>() {
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
                                "yyyy-MM-dd HH:mm:ss");

                            @Override
                            public long extractTimestamp(
                                Tuple3<String, String, Long> stringStringLongTuple3, long l) {
                                try {
                                    return simpleDateFormat.parse(stringStringLongTuple3.f1)
                                        .getTime();
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                return 0;
                            }
                        };
                    }
                });
    }
}
