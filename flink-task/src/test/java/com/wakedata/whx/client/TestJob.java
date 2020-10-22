package com.wakedata.whx.client;

import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.DiscardingSink;
import org.junit.Test;

/**
 * @author :wanghuxiong
 * @title: TestJob
 * @projectName flink-study
 * @description: TODO
 * @date 2020/10/20 10:43 下午
 */
public class TestJob {

    @Test
    public void testExecuteJob() throws Exception {
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        DataStreamSource<Integer> source = env.fromElements(1, 2, 3, 4);

        SingleOutputStreamOperator<Integer> mapper = source.map(element -> {
            return 2 * element;
        });
        mapper.addSink(new DiscardingSink<>());
        env.execute();
    }
}






















