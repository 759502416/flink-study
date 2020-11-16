package com.wakedata.whx.datastream.state;

import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.api.common.state.*;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.configuration.Configuration;

/**
 * @author :wanghuxiong
 * @title: CountWindowAverage
 * @projectName flink-study
 * @description: TODO
 * @date 2020/11/10 10:47 下午
 */
public class CountWindowAverage extends RichMapFunction<Tuple3<String, String, Long>, Tuple3<String, String, Long>> {

    /**
     * 计算键值状态
     */
    private transient ValueState<Tuple2<String, Long>> sum;

    //ListState

    //ReducingState

    //MapState

    @Override
    public Tuple3<String, String, Long> map(Tuple3<String, String, Long> value) throws Exception {

        Tuple2<String, Long> oldValue = sum.value();
        if (oldValue == null) {
            sum.update(Tuple2.of(value.f0, value.f2));
        } else {
            sum.update(Tuple2.of(value.f0, value.f2 + oldValue.f1));
        }
        System.err.println("状态为:" + sum.value().toString());
        return value;
    }

    @Override
    public void open(Configuration parameters) throws Exception {
        // 获取ttl
        StateTtlConfig ttlConfig = StateTtlConfig
                .newBuilder(Time.seconds(1))
                .setUpdateType(StateTtlConfig.UpdateType.OnCreateAndWrite)
                .setStateVisibility(StateTtlConfig.StateVisibility.NeverReturnExpired)
                .build();

        ValueStateDescriptor<Tuple2<String, Long>> descriptor = new ValueStateDescriptor(
                "average",
                TypeInformation.of(new TypeHint<Tuple2<String, Long>>() {
                    @Override
                    public TypeInformation<Tuple2<String, Long>> getTypeInfo() {
                        return super.getTypeInfo();
                    }
                })
        );
        // 设置ttl
        descriptor.enableTimeToLive(ttlConfig);
        sum = getRuntimeContext().getState(descriptor);
    }

    @Override
    public void close() throws Exception {
        super.close();
    }
}
