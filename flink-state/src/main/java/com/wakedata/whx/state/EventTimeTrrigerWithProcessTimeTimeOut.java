package com.wakedata.whx.state;

import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.streaming.api.windowing.triggers.Trigger;
import org.apache.flink.streaming.api.windowing.triggers.TriggerResult;
import org.apache.flink.streaming.api.windowing.windows.Window;

/**
 * @author wanghx
 * @describe
 * @since 2022/5/12 15:09
 */
@Slf4j
public class EventTimeTrrigerWithProcessTimeTimeOut<T> extends Trigger<T, Window> {

    /**
     * 超时的处理时间
     */
    private final Long timeOutProcessTime;

    /**
     * 用于储存窗口当前数据量的状态对象
     */
    ValueStateDescriptor<Long> lastTempDescriptor = new ValueStateDescriptor<Long>(
            "last-value-timestamp",
            Long.class);

    public EventTimeTrrigerWithProcessTimeTimeOut(Long timeOutProcessTime) {
        this.timeOutProcessTime = timeOutProcessTime;
    }


    private TriggerResult fireAndPurge(Window window, TriggerContext ctx) throws Exception {
        clear(window, ctx);
        return TriggerResult.FIRE_AND_PURGE;
    }

    @Override
    public TriggerResult onElement(T element, long timestamp, Window window, TriggerContext ctx) throws Exception {
        ValueState<Long> partitionedState = ctx.getPartitionedState(lastTempDescriptor);
        long nowProcessTime = ctx.getCurrentProcessingTime();
        if (partitionedState.value() == null) {
            partitionedState.update(nowProcessTime);
        }
        Long lastProcessTime = partitionedState.value();
        if (timestamp >= window.maxTimestamp()) {
            log.debug("fire with tiem: {}", timestamp);
            return fireAndPurge(window, ctx);
        } else {
            return TriggerResult.CONTINUE;
        }
    }

    @Override
    public TriggerResult onProcessingTime(long time, Window window, TriggerContext ctx) throws Exception {
        long nowProcessTime = ctx.getCurrentProcessingTime();
        if (time < window.maxTimestamp()) {
            return TriggerResult.CONTINUE;
        } else {
            log.debug("fire with process tiem: {}", time);
            return fireAndPurge(window, ctx);
        }
    }

    @Override
    public TriggerResult onEventTime(long time, Window window, TriggerContext ctx) throws Exception {
        long nowProcessTime = ctx.getCurrentProcessingTime();
        if (time < window.maxTimestamp()) {
            return TriggerResult.CONTINUE;
        } else {
            log.debug("fire with event tiem: {}", time);
            return fireAndPurge(window, ctx);
        }
    }

    @Override
    public void clear(Window window, TriggerContext ctx) throws Exception {
        ValueState<Long> partitionedState = ctx.getPartitionedState(lastTempDescriptor);
        partitionedState.clear();
    }
}
