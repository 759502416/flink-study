package com.wakedata.learn;

import com.alibaba.fastjson.JSONObject;
import com.wakedata.whx.EnvironmentUtil;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.cep.CEP;
import org.apache.flink.cep.PatternStream;
import org.apache.flink.cep.functions.PatternProcessFunction;
import org.apache.flink.cep.nfa.aftermatch.AfterMatchSkipStrategy;
import org.apache.flink.cep.pattern.Pattern;
import org.apache.flink.cep.pattern.conditions.SimpleCondition;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.PrintSinkFunction;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.util.Collector;
import org.apache.kafka.clients.consumer.ConsumerConfig;

/**
 * TODO
 *
 * @author: WangHuXiong
 * @title: ComplexCepTest
 * @date: 2021-01-13 10:33
 * @program: FlinkStudy
 * @description:
 **/
public class ComplexCepTest {

    public static final String KAFKA_TOPIC = "whx_cep_test_topic2";

    public static final String KAFKA_BOOTSTRAP_SERVER = "hd-node-3-41.wakedata.com:6667,hd-node-3-42.wakedata.com:6667,hd-node-3-43.wakedata.com:6667";

    public static final String KAFKA_CONSUMER_GROUP_ID = "test_cep1";

    public static final String sVidUserName = "wake1";

    public static final String vidUserName = "wake2";

    public static void main(String[] args) throws Exception {
        // 开启checkPoint
        EnvironmentUtil.turnOnCheckpoint();
        // 平台自动化获取flink连接属性
        StreamExecutionEnvironment streamExecutionEnvironment = EnvironmentUtil
            .getStreamExecutionEnvironment();
        // 设置flinkConsumer连接属性
        Properties properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_BOOTSTRAP_SERVER);
        // 消费groupId 使用当前任务id
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, KAFKA_CONSUMER_GROUP_ID);
        // 选择kafka消费规则
        properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        // 设置kafka的topic名称
        FlinkKafkaConsumer<String> stringFlinkKafkaConsumer = new FlinkKafkaConsumer<>(KAFKA_TOPIC,
            new SimpleStringSchema(), properties);
        // 添加实时环境，并添加kafka 数据源
        DataStreamSource<String> dataStreamSource = streamExecutionEnvironment
            .addSource(stringFlinkKafkaConsumer);
        // TODO 获得对象
        SingleOutputStreamOperator<Tuple3<Integer, Integer, String>> singleOutputStreamOperator = dataStreamSource
            .map(new RichMapFunction<String, Tuple3<Integer, Integer, String>>() {
                @Override
                public Tuple3<Integer, Integer, String> map(String s) throws Exception {
                    JSONObject jsonObject = JSONObject.parseObject(s);
                    return Tuple3.of(Integer.valueOf(jsonObject.get("id").toString()),
                        Integer.valueOf(jsonObject.get("num").toString()),
                        jsonObject.get("name").toString());
                }
            });
        KeyedStream<Tuple3<Integer, Integer, String>, Integer> keyedStream = singleOutputStreamOperator
            .keyBy(new KeySelector<Tuple3<Integer, Integer, String>, Integer>() {
                @Override
                public Integer getKey(Tuple3<Integer, Integer, String> integerIntegerTuple2)
                    throws Exception {
                    // f0这些可以根据index 获取
                    return integerIntegerTuple2.f0;
                }
            });
        // begin AfterMatchSkipStrategy.noSkip() 不会发生变化
        //
        Pattern<Tuple3<Integer, Integer, String>, Tuple3<Integer, Integer, String>> pattern = Pattern.<Tuple3<Integer, Integer, String>>begin(
            "begin", AfterMatchSkipStrategy.skipToNext())
            .where(
                new SimpleCondition<Tuple3<Integer, Integer, String>>() {
                    @Override
                    public boolean filter(Tuple3<Integer, Integer, String> integerIntegerTuple2)
                        throws Exception {
                        return integerIntegerTuple2.f1 == 0;
                    }
                }
                // 期望出现0到多次，并且尽可能的重复次数多
            ).oneOrMore().optional().greedy()
            .next("one").where(
                new SimpleCondition<Tuple3<Integer, Integer, String>>() {
                    @Override
                    public boolean filter(Tuple3<Integer, Integer, String> integerIntegerTuple2)
                        throws Exception {
                        return integerIntegerTuple2.f1 == 1;
                    }
                }
                // 期望出现0、2、3或者4次，并且尽可能的重复次数多
            ).times(2, 4).greedy()
            .next("two").where(
                new SimpleCondition<Tuple3<Integer, Integer, String>>() {
                    @Override
                    public boolean filter(Tuple3<Integer, Integer, String> integerIntegerTuple2)
                        throws Exception {
                        return integerIntegerTuple2.f1 == 2;
                    }
                }
                // 期望出现4次
            ).times(4)
             .next("three").where(
                new SimpleCondition<Tuple3<Integer, Integer, String>>() {
                    @Override
                    public boolean filter(Tuple3<Integer, Integer, String> integerIntegerTuple2)
                        throws Exception {
                        return integerIntegerTuple2.f1 == 3;
                    }
                }
                // 期望出现0、2或多次，并且尽可能的重复次数多
            ).timesOrMore(2).greedy()
            .next("four").where(
                new SimpleCondition<Tuple3<Integer, Integer, String>>() {
                    @Override
                    public boolean filter(
                        Tuple3<Integer, Integer, String> integerIntegerStringTuple3)
                        throws Exception {
                        return integerIntegerStringTuple3.f1 == 4;
                    }
                }
            );
        PatternStream<Tuple3<Integer, Integer, String>> patternStream = CEP
            .pattern(keyedStream, pattern)
            .inProcessingTime();
        PatternProcessFunction<Tuple3<Integer, Integer, String>, Tuple3<Integer, Integer, String>> patternProcessFunction = new PatternProcessFunction<Tuple3<Integer, Integer, String>, Tuple3<Integer, Integer, String>>() {
            @Override
            public void processMatch(Map<String, List<Tuple3<Integer, Integer, String>>> match,
                Context ctx,
                Collector<Tuple3<Integer, Integer, String>> out) throws Exception {
                Set<String> strings = match.keySet();
                strings.forEach(key -> {
                    System.err.println("key 为：" + key);
                    match.get(key).forEach(
                        value -> {
                            System.err.println("输出：" + value.toString());
                        }
                    );
                });
            }
        };
        patternStream.process(patternProcessFunction).addSink(new PrintSinkFunction<>());
        streamExecutionEnvironment.execute();
    }
}
