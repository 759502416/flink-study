package com.wakedata.whx.datastream.exception;

import com.wakedata.whx.EnvironmentUtil;
import lombok.SneakyThrows;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStreamSink;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;

/**
 * @author :wanghuxiong
 * @title: TestRunTImeException
 * @projectName flink-study
 * @description: TODO
 * @date 2021/1/4 9:59 下午
 */
public class TestRunTimeException {

    @SneakyThrows
    public static void main(String[] args) {
        StreamExecutionEnvironment streamExecutionEnvironment = EnvironmentUtil.getStreamExecutionEnvironment();
        DataStreamSource<Tuple3<String, String, Long>> tuple3DataStreamSource = streamExecutionEnvironment.fromCollection(EnvironmentUtil.tuple3StringStringLongElements);
        DataStreamSink<Tuple3<String, String, Long>> close = tuple3DataStreamSource.addSink(new RichSinkFunction<Tuple3<String, String, Long>>() {
            @Override
            public void invoke(Tuple3<String, String, Long> value, Context context) throws Exception {
                throw new RuntimeException();
            }

            @Override
            public void close() throws Exception {
                System.err.println("close");
                super.close();
            }
        });
        streamExecutionEnvironment.execute();
    }
}
