package com.wakedata.whx.state;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.state.MapState;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.common.state.StateTtlConfig;
import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.contrib.streaming.state.RocksDBStateBackend;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

import java.util.concurrent.TimeUnit;

/**
 * @author wanghx
 * @describe
 * @since 2022/7/17 16:01
 */
public class FlinkKeyStateTest {

    public static void main(String[] args) throws Exception {
        Configuration configuration = new Configuration();
        configuration.setLong("web.timeout",10L * 1000L);
        configuration.setLong("akka.ask.timeout",10L * 1000L);
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment(configuration);
        env.setStreamTimeCharacteristic(TimeCharacteristic.ProcessingTime);
        env.setStateBackend(new RocksDBStateBackend("file:///rockdbtmp//tmp"));

        DataStreamSource<String> stringDataStreamSource = env.socketTextStream("127.0.0.1", 28088, "&&&");

        KeyedStream<TestPojo, String> testPojoStringKeyedStream = stringDataStreamSource.map(new MapFunction<String, TestPojo>() {
            @Override
            public TestPojo map(String s) throws Exception {
                return JSONObject.parseObject(s, TestPojo.class);
            }
        }).keyBy(new KeySelector<TestPojo, String>() {
            @Override
            public String getKey(TestPojo testPojo) throws Exception {
                return testPojo.gameCode;
            }
        });
        testPojoStringKeyedStream.window(TumblingProcessingTimeWindows.of(Time.of(10, TimeUnit.SECONDS)))
                .process(new ProcessWindowFunction<TestPojo, Integer, String, TimeWindow>() {

                    private transient MapState<String, Integer> mapState = null;

                    @Override
                    public void open(Configuration parameters) throws Exception {
                        super.open(parameters);
                        // 设置生命有效期为两天
                        StateTtlConfig ttlConfig = StateTtlConfig
                                .newBuilder(org.apache.flink.api.common.time.Time.days(2))
                                .setUpdateType(StateTtlConfig.UpdateType.OnReadAndWrite)
                                .setStateVisibility(StateTtlConfig.StateVisibility.NeverReturnExpired)
                                .build();
                        MapStateDescriptor<String, Integer> mapStateDescriptor = new MapStateDescriptor<>("cache-map-state", BasicTypeInfo.STRING_TYPE_INFO, BasicTypeInfo.INT_TYPE_INFO);
                        //mapStateDescriptor.enableTimeToLive(ttlConfig);
                        mapState = getRuntimeContext().getMapState(mapStateDescriptor);
                    }

                    @Override
                    public void close() throws Exception {
                        super.close();
                    }

                    @Override
                    public void process(String s, Context context, Iterable<TestPojo> elements, Collector<Integer> out) throws Exception {

                        Integer integer = mapState.get(s);
                        if (integer == null) {
                            integer = 0;
                        }
                        integer++;
                        mapState.put(s, integer);
                        out.collect(integer);
                    }
                }).print();
        testPojoStringKeyedStream.window(TumblingProcessingTimeWindows.of(Time.of(15, TimeUnit.SECONDS)))
                .process(new ProcessWindowFunction<TestPojo, Integer, String, TimeWindow>() {

                    private transient MapState<String, Integer> mapState = null;

                    @Override
                    public void open(Configuration parameters) throws Exception {
                        super.open(parameters);
                        // 设置生命有效期为两天
                        StateTtlConfig ttlConfig = StateTtlConfig
                                .newBuilder(org.apache.flink.api.common.time.Time.days(2))
                                .setUpdateType(StateTtlConfig.UpdateType.OnReadAndWrite)
                                .setStateVisibility(StateTtlConfig.StateVisibility.NeverReturnExpired)
                                .build();
                        MapStateDescriptor<String, Integer> mapStateDescriptor = new MapStateDescriptor<>("cache-map-state", BasicTypeInfo.STRING_TYPE_INFO, BasicTypeInfo.INT_TYPE_INFO);
                        //mapStateDescriptor.enableTimeToLive(ttlConfig);
                        mapState = getRuntimeContext().getMapState(mapStateDescriptor);
                    }

                    @Override
                    public void close() throws Exception {
                        super.close();
                    }

                    @Override
                    public void process(String s, Context context, Iterable<TestPojo> elements, Collector<Integer> out) throws Exception {

                        Integer integer = mapState.get(s);
                        if (integer == null) {
                            integer = 0;
                        }
                        integer+=2;
                        mapState.put(s, integer);
                        out.collect(integer);
                    }
                }).print();

        env.execute();

    }

    @Data
    private static class TestPojo {
        private String gameCode;

        private Integer price;
    }
}
