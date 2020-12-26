package com.wakedata.whx;


import com.alibaba.fastjson.JSONObject;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.cep.CEP;
import org.apache.flink.cep.PatternStream;
import org.apache.flink.cep.functions.PatternProcessFunction;
import org.apache.flink.cep.pattern.Pattern;
import org.apache.flink.cep.pattern.conditions.SimpleCondition;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.util.Collector;
import org.apache.kafka.clients.consumer.ConsumerConfig;


/**
 * TODO
 *
 * @author: WangHuXiong
 * @title: FlinkCepRunner
 * @date: 2020-12-26 17:54
 * @program: FlinkStudy
 * @description:
 **/
public class FlinkCepRunner {

    public static final String KAFKA_TOPIC = "whx_cep_topic";

    public static final String KAFKA_BOOTSTRAP_SERVER = "hd-node-3-41.wakedata.com:6667,hd-node-3-42.wakedata.com:6667,hd-node-3-43.wakedata.com:6667";

    public static final String KAFKA_CONSUMER_GROUP_ID = "test_cep";

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
        // 解析kafka的数据，按照预定格式解析
        KeyedStream<Tuple3<Long, String, Long>, Long> tempData = dataStreamSource
            .map(new MapFunction<String, Tuple3<Long, String, Long>>() {
                @Override
                public Tuple3<Long, String, Long> map(String data) throws Exception {
                    JSONObject jsonObject = JSONObject.parseObject(data);
                    Tuple3<Long, String, Long> parseString = Tuple3
                        .of(jsonObject.getLong("id"), jsonObject.getString("name"),
                            jsonObject.getLong("money"));
                    return parseString;
                }
                // 选择主键未id
            }).keyBy(new KeySelector<Tuple3<Long, String, Long>, Long>() {
                @Override
                public Long getKey(Tuple3<Long, String, Long> longStringLongTuple3)
                    throws Exception {
                    return longStringLongTuple3.f0;
                }
            });

        Pattern<Tuple3<Long, String, Long>, Tuple3<Long, String, Long>> begin = Pattern.<Tuple3<Long, String, Long>>begin(
            "begin").where(
            new SimpleCondition<Tuple3<Long, String, Long>>() {
                @Override
                public boolean filter(Tuple3<Long, String, Long> longStringLongTuple3)
                    throws Exception {
                    return longStringLongTuple3.f2 > 5L;
                }
            }
        );
        Pattern<Tuple3<Long, String, Long>, Tuple3<Long, String, Long>> sVipPattern = begin
            .next("sVip").where(new SimpleCondition<Tuple3<Long, String, Long>>() {
                @Override
                public boolean filter(Tuple3<Long, String, Long> longStringLongTuple3)
                    throws Exception {
                    return longStringLongTuple3.f1.equals(sVidUserName);
                }
            });
        Pattern<Tuple3<Long, String, Long>, Tuple3<Long, String, Long>> vipPattern = begin
            .next("vip").where(new SimpleCondition<Tuple3<Long, String, Long>>() {
                @Override
                public boolean filter(Tuple3<Long, String, Long> longStringLongTuple3)
                    throws Exception {
                    return longStringLongTuple3.f1.equals(vidUserName);
                }
            });
        Pattern<Tuple3<Long, String, Long>, Tuple3<Long, String, Long>> normalPattern = begin
            .next("normal").where(new SimpleCondition<Tuple3<Long, String, Long>>() {
                @Override
                public boolean filter(Tuple3<Long, String, Long> longStringLongTuple3)
                    throws Exception {
                    return !longStringLongTuple3.f1.equals(sVidUserName) && !longStringLongTuple3.f1
                        .equals(vidUserName);
                }
            });
        PatternStream<Tuple3<Long, String, Long>> sVipPatternStream = CEP.pattern(tempData, sVipPattern);
        PatternStream<Tuple3<Long, String, Long>> vipPatternStream = CEP.pattern(tempData, vipPattern);
        PatternStream<Tuple3<Long, String, Long>> normalPatternStream = CEP.pattern(tempData, normalPattern);
        PatternProcessFunction<Tuple3<Long, String, Long>, Object> patternProcessFunction = new PatternProcessFunction<Tuple3<Long, String, Long>, Object>() {
            @Override
            public void processMatch(Map<String, List<Tuple3<Long, String, Long>>> map,
                Context context, Collector<Object> collector) throws Exception {
                collector.collect(map.toString());
            }
        };
        sVipPatternStream.process(patternProcessFunction).print();
        vipPatternStream.process(patternProcessFunction).print();
        normalPatternStream.process(patternProcessFunction).print();
        streamExecutionEnvironment.execute("CEP");
    }

}
