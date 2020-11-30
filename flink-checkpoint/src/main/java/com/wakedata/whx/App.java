package com.wakedata.whx;

import com.wakedata.whx.sink.MyTestCheckPointSink;
import java.util.Properties;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        StreamExecutionEnvironment streamExecutionEnvironment = EnvironmentUtil
            .getStreamExecutionEnvironment();
        Properties props = new Properties();
        props.setProperty("bootstrap.servers", "hd-node-3-41.wakedata.com:6667,hd-node-3-42.wakedata.com:6667,hd-node-3-43.wakedata.com:6667");
        // 消费groupid 使用当前任务id
        props.setProperty("group.id", "whx-test1");
        props.setProperty("auto.offset.reset","earliest");
        FlinkKafkaConsumer<String> whx = new FlinkKafkaConsumer<>("whx", new SimpleStringSchema(),
            props);
        DataStreamSource<String> dataStreamSource = streamExecutionEnvironment.addSource(whx);
        dataStreamSource.addSink(new MyTestCheckPointSink());
        try {
            streamExecutionEnvironment.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
