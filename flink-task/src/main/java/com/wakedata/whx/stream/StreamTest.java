package com.wakedata.whx.stream;

import com.alibaba.fastjson.JSONObject;
import com.wakedata.whx.window.SlidingProcessTimeWindows;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.state.MapState;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.datastream.WindowedStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.SlidingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.assigners.WindowStagger;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

/**
 * @author :wanghuxiong
 * @title: StreamTest
 * @projectName flink-study
 * @description: TODO
 * @date 2021/10/11 4:59 下午
 */
public class StreamTest {
// {"name":"whx"}&
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStreamSource<String> streamSource = env.socketTextStream("0.0.0.0", 28088, "&");
        SingleOutputStreamOperator<JSONObject> map = streamSource.map(new MapFunction<String, JSONObject>() {
            @Override
            public JSONObject map(String s) throws Exception {
                return JSONObject.parseObject(s);
            }
        });
        KeyedStream<JSONObject, String> keyedStream = map.keyBy(new KeySelector<JSONObject, String>() {
            @Override
            public String getKey(JSONObject jsonObject) throws Exception {
                return jsonObject.getString("name");
            }
        });
        WindowedStream<JSONObject, String, TimeWindow> window = keyedStream.window(TumblingProcessingTimeWindows.of(Time.seconds(3)));
        window.process(new ProcessWindowFunction<JSONObject, Tuple2<String,Integer>, String, TimeWindow>() {

            private MapState<String, Integer> mapState = null;

            @Override
            public void open(Configuration parameters) throws Exception {
                super.open(parameters);
                MapStateDescriptor<String, Integer> mapStateDescriptor = new MapStateDescriptor<String, Integer>("wc", TypeInformation.of(new TypeHint<String>() {
                    @Override
                    public TypeInformation<String> getTypeInfo() {
                        return super.getTypeInfo();
                    }
                }), TypeInformation.of(new TypeHint<Integer>() {
                    @Override
                    public TypeInformation<Integer> getTypeInfo() {
                        return super.getTypeInfo();
                    }
                }));
                mapState = getRuntimeContext().getMapState(mapStateDescriptor);
            }

            @Override
            public void close() throws Exception {
                super.close();
            }

            @Override
            public void process(String s, Context context, Iterable<JSONObject> elements, Collector<Tuple2<String, Integer>> out) throws Exception {
                Integer cacheAvgSum = mapState.get(s);
                if (cacheAvgSum == null) {
                    cacheAvgSum = 0;
                }
                for (JSONObject element : elements) {
                    cacheAvgSum++;
                }
                mapState.put(s,cacheAvgSum);
                out.collect(Tuple2.of(s,cacheAvgSum));
            }
        }).addSink(new RichSinkFunction<Tuple2<String, Integer>>() {
            @Override
            public void invoke(Tuple2<String, Integer> value, Context context) throws Exception {
                System.err.println(value);
            }
        });
        env.execute();
    }
}
