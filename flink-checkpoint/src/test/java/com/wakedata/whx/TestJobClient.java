package com.wakedata.whx;

import com.alibaba.fastjson.JSONObject;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.api.common.state.MapState;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.common.state.StateTtlConfig;
import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.core.execution.JobClient;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

/**
 * @author wanghx
 * @describe
 * @since 2022/3/7 14:14
 */
public class TestJobClient {

    @Test
    public void testSavepoint() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.enableCheckpointing(1000L, CheckpointingMode.EXACTLY_ONCE);
        env.getCheckpointConfig().setMinPauseBetweenCheckpoints(500);
        // Checkpoint 必须在一分钟内完成，否则就会被抛弃
        env.getCheckpointConfig().setCheckpointTimeout(60000);
        // 允许两个连续的 checkpoint 错误
        env.getCheckpointConfig().setTolerableCheckpointFailureNumber(2);
        // 同一时间只允许一个 checkpoint 进行
        env.getCheckpointConfig().setMaxConcurrentCheckpoints(1);
        // 使用 externalized checkpoints，这样 checkpoint 在作业取消后仍就会被保留
        env.getCheckpointConfig().enableExternalizedCheckpoints(
                CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);
        env.getCheckpointConfig().setCheckpointStorage("file:///flink_dir//checkpoint");

        DataStreamSource<String> stringDataStreamSource = env.socketTextStream("127.0.0.1", 28088, "&&&");
        stringDataStreamSource.map(new RichMapFunction<String, JSONObject>() {
            @Override
            public JSONObject map(String s) throws Exception {
                return JSONObject.parseObject(s);
            }
        }).keyBy(new KeySelector<JSONObject, String>() {
            @Override
            public String getKey(JSONObject jsonObject) throws Exception {
                return jsonObject.getString("name");
            }
        }).window(TumblingProcessingTimeWindows.of(Time.of(5, TimeUnit.SECONDS)))
                .process(new ProcessWindowFunction<JSONObject, Integer, String, TimeWindow>() {

                    private transient MapState<String, Integer> mapState = null;

                    @Override
                    public void open(Configuration parameters) throws Exception {
                        super.open(parameters);

                        MapStateDescriptor<String, Integer> keySumStates = new MapStateDescriptor<>("key_sum_states",
                                BasicTypeInfo.STRING_TYPE_INFO,
                                BasicTypeInfo.INT_TYPE_INFO);
                        // 设置生命有效期为两天
                        StateTtlConfig ttlConfig = StateTtlConfig
                                .newBuilder(org.apache.flink.api.common.time.Time.days(1))
                                .setUpdateType(StateTtlConfig.UpdateType.OnReadAndWrite)
                                .setStateVisibility(StateTtlConfig.StateVisibility.NeverReturnExpired)
                                .build();
                        keySumStates.enableTimeToLive(ttlConfig);
                        mapState = getRuntimeContext().getMapState(keySumStates);
                    }

                    @Override
                    public void close() throws Exception {
                        super.close();
                    }

                    @Override
                    public void process(String s, Context context, Iterable<JSONObject> elements, Collector<Integer> out) throws Exception {
                        Integer sumValue = mapState.get(s);
                        if (sumValue == null) {
                            sumValue = 0;
                        }
                        for (JSONObject element : elements) {
                            sumValue++;
                        }
                        mapState.put(s, sumValue);
                        out.collect(sumValue);
                    }
                }).print();
         env.execute();

    }
}
