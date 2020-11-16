package com.wakedata.whx.datastream.state;

import org.apache.flink.api.common.state.ListState;
import org.apache.flink.api.common.state.ListStateDescriptor;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.runtime.state.FunctionInitializationContext;
import org.apache.flink.runtime.state.FunctionSnapshotContext;
import org.apache.flink.streaming.api.checkpoint.CheckpointedFunction;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;

import java.util.ArrayList;
import java.util.List;

/**
 * @author :wanghuxiong
 * @title: BufferingSink
 * @projectName flink-study
 * @description: TODO
 * @date 2020/11/10 11:36 下午
 */
public class BufferingSink implements SinkFunction<Tuple2<String, Long>>, CheckpointedFunction {

    private final int threshold;

    private transient ListState<Tuple2<String, Long>> checkPointedState;

    private List<Tuple2<String, Long>> bufferedElements;

    public BufferingSink(int threshold) {
        this.threshold = threshold;
        this.bufferedElements = new ArrayList<>();
    }

    @Override
    public void invoke(Tuple2<String, Long> value, Context context) throws Exception {
        bufferedElements.add(value);
        if (bufferedElements.size() == threshold) {
            // send it to the sink
            // 假装输出了
            bufferedElements.forEach(
                    element -> {
                        System.err.println("Buffering 输出消息为:" + element.toString());
                    }
            );
            // ...
        }
        bufferedElements.clear();
    }

    @Override
    public void snapshotState(FunctionSnapshotContext functionSnapshotContext) throws Exception {
        checkPointedState.clear();
        for (Tuple2<String, Long> bufferedElement : bufferedElements) {
            checkPointedState.add(bufferedElement);
        }
    }

    @Override
    public void initializeState(FunctionInitializationContext functionInitializationContext) throws Exception {
        ListStateDescriptor<Tuple2<String, Long>> descriptor = new ListStateDescriptor(
                "buffered-elements",
                TypeInformation.of(new TypeHint<Tuple2<String, Long>>() {
                    @Override
                    public TypeInformation<Tuple2<String, Long>> getTypeInfo() {
                        return super.getTypeInfo();
                    }
                })
        );
        checkPointedState = functionInitializationContext.getOperatorStateStore().getListState(descriptor);
        if (functionInitializationContext.isRestored()) {
            for (Tuple2<String, Long> element : checkPointedState.get()) {
                bufferedElements.add(element);
            }
        }
    }
}
