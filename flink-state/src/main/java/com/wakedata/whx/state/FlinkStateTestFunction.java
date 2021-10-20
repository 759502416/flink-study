package com.wakedata.whx.state;

import com.wakedata.whx.EnvironmentUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.common.state.StateTtlConfig;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.runtime.state.memory.MemoryStateBackend;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.concurrent.TimeUnit;

/**
 * @author :wanghuxiong
 * @title: FlinkStateTestFunction
 * @projectName flink-study
 * @description: TODO
 * @date 2021/4/27 11:53 下午
 */
@Slf4j
public class FlinkStateTestFunction {

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment streamEnv = EnvironmentUtil.getStreamExecutionEnvironment();

        MemoryStateBackend memoryStateBackend = new MemoryStateBackend();
        // 设置状态后端为内存状态后端
        streamEnv.setStateBackend(memoryStateBackend);
        // 设置重启策略为三次
        streamEnv.setRestartStrategy(RestartStrategies.fixedDelayRestart(3,
                Time.of(3, TimeUnit.SECONDS)));
        // 设置模式为精确一次
        streamEnv.getCheckpointConfig().setCheckpointInterval(3000L);

        DataStreamSource<String> stringDataStreamSource = streamEnv.socketTextStream("127.0.0.1", 28088,"&");

        stringDataStreamSource.map(new MapFunction<String, Tuple2<String, Integer>>() {
            @Override
            public Tuple2<String, Integer> map(String s) throws Exception {

                if (s.equals("abc")) {
                    System.err.println("abc");
                    throw new Exception("this is a abc!!!!");
                }
                return Tuple2.of(s, 1);
            }
        }).keyBy(x -> x.f0).sum(1).print();

        log.info("Abc");
        streamEnv.execute("test state");
    }
}
