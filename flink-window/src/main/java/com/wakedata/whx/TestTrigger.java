package com.wakedata.whx;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.flink.api.common.eventtime.*;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

import java.util.concurrent.TimeUnit;

/**
 * @author wanghx
 * @describe
 * @since 2022/5/12 14:47
 */
public class TestTrigger {

    @Getter
    @Setter
    @ToString
    private static class Test {
        private String name;

        private String gameCode;

        private String dataTime;

        public Test() {
        }

        public Test(String name, String gameCode, String dataTime) {
            this.name = name;
            this.gameCode = gameCode;
            this.dataTime = dataTime;
        }

        public Long getDataTimeStamp() {
            return DateUtil.parse(dataTime).getTime();
        }

    }

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        DataStreamSource<String> textStream = env.socketTextStream("127.0.0.1", 28088, "&&&");
        SingleOutputStreamOperator<Test> testPojo = textStream.map(new MapFunction<String, Test>() {
            @Override
            public Test map(String s) throws Exception {
                return JSONObject.parseObject(s, Test.class);
            }
        });

        SingleOutputStreamOperator<Test> testWithWater = testPojo.assignTimestampsAndWatermarks(new WatermarkStrategy<Test>() {
            final long maxOutOfOrderness = 0L;

            @Override
            public WatermarkGenerator<Test> createWatermarkGenerator(WatermarkGeneratorSupplier.Context context) {
                return new WatermarkGenerator<Test>() {
                    long currentMaxTimestamp = 0L;

                    @Override
                    public void onEvent(Test test, long l, WatermarkOutput watermarkOutput) {
                        currentMaxTimestamp = Math
                                .max(currentMaxTimestamp, l - maxOutOfOrderness);
                        System.err.println("水位线等于：" + currentMaxTimestamp);
                        watermarkOutput.emitWatermark(new Watermark(currentMaxTimestamp));
                    }

                    @Override
                    public void onPeriodicEmit(WatermarkOutput watermarkOutput) {
                    }
                };
            }

            @Override
            public TimestampAssigner<Test> createTimestampAssigner(TimestampAssignerSupplier.Context context) {
                return new TimestampAssigner<Test>() {
                    @Override
                    public long extractTimestamp(Test test, long l) {
                        return test.getDataTimeStamp();
                    }
                };
            }
        });

        testWithWater.keyBy(new KeySelector<Test, String>() {
            @Override
            public String getKey(Test test) throws Exception {
                return test.getGameCode();
            }
        }).window(TumblingEventTimeWindows.of(Time.of(5, TimeUnit.SECONDS)))
                .trigger(new EventTimeTrrigerWithProcessTimeTimeOut<>(10000L))
                .process(new ProcessWindowFunction<Test, Iterable<Test>, String, TimeWindow>() {
            @Override
            public void process(String s, Context context, Iterable<Test> elements, Collector<Iterable<Test>> out) throws Exception {
                out.collect(elements);
            }
        }).addSink(new RichSinkFunction<Iterable<Test>>() {
            @Override
            public void invoke(Iterable<Test> value, Context context) throws Exception {
                super.invoke(value, context);
                value.forEach(System.out::println);
            }
        });



        env.execute();
    }
}
