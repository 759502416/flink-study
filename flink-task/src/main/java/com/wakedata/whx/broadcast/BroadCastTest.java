package com.wakedata.whx.broadcast;

import ch.qos.logback.core.joran.conditional.ThenAction;
import com.alibaba.fastjson.JSONObject;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.common.state.ReadOnlyBroadcastState;
import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.BroadcastStream;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.co.KeyedBroadcastProcessFunction;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.apache.flink.util.Collector;

/**
 * @author wanghx
 * @describe 广播流的例子编写，进行测试
 * @since 2021/10/12 16:16
 */
public class BroadCastTest {

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        SingleOutputStreamOperator<JSONObject> source = env.socketTextStream("0.0.0.0", 28088, "&").map(new RichMapFunction<String, JSONObject>() {
            @Override
            public JSONObject map(String s) throws Exception {
                return JSONObject.parseObject(s);
            }
        });

        KeyedStream<JSONObject, Tuple2<String, String>> colorPartitionedStream = source.keyBy(new KeySelector<JSONObject, Tuple2<String, String>>() {
            @Override
            public Tuple2<String, String> getKey(JSONObject jsonObject) throws Exception {
                return Tuple2.of(jsonObject.getString("item"), jsonObject.getString("color"));
            }
        });

        // {"item":"fang","color":"red"}
        SingleOutputStreamOperator<JSONObject> ruleStream = env.socketTextStream("0.0.0.0", 28089, "&").map(new RichMapFunction<String, JSONObject>() {
            @Override
            public JSONObject map(String s) throws Exception {
                return JSONObject.parseObject(s);
            }
        });

        BroadcastStream<JSONObject> rulesBroadcastStream = ruleStream.broadcast(new MapStateDescriptor<String, String>("RulesBroadcastState",
                BasicTypeInfo.STRING_TYPE_INFO,
                BasicTypeInfo.STRING_TYPE_INFO));

        colorPartitionedStream.connect(rulesBroadcastStream).process(new KeyedBroadcastProcessFunction<Tuple2<String, String>, JSONObject, JSONObject, Tuple2<String, String>>() {

            // 与之前的 ruleStateDescriptor 相同
            MapStateDescriptor<String, String> ruleStateDescriptor = new MapStateDescriptor<String, String>("RulesBroadcastState",
                    BasicTypeInfo.STRING_TYPE_INFO,
                    BasicTypeInfo.STRING_TYPE_INFO);

            @Override
            public void processElement(JSONObject value, ReadOnlyContext ctx, Collector<Tuple2<String, String>> out) throws Exception {
                ReadOnlyBroadcastState<String, String> broadcastMap = ctx.getBroadcastState(ruleStateDescriptor);
                String item = value.getString("item");
                String color = value.getString("color");
                String matchValue = broadcastMap.get(item);
                if (matchValue == null) {
                    Thread.sleep(30000);
                    broadcastMap.get(item);
                }
                if (color.equals(matchValue)) {
                    out.collect(Tuple2.of(item, "ok"));
                }else {
                    out.collect(Tuple2.of(item, "notOk"));
                }
            }

            @Override
            public void processBroadcastElement(JSONObject value, Context ctx, Collector<Tuple2<String, String>> out) throws Exception {
                String item = value.getString("item");
                String color = value.getString("color");
                ctx.getBroadcastState(ruleStateDescriptor).put(item, color);
              //  out.collect(Tuple2.of(item, color));
            }
        }).addSink(new RichSinkFunction<Tuple2<String, String>>() {
            @Override
            public void invoke(Tuple2<String, String> value, Context context) throws Exception {
                super.invoke(value, context);
                System.err.println(value);
            }
        });


        env.execute();
    }
}
