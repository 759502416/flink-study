package com.wakedata.whx;

import cn.hutool.core.date.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.streaming.api.windowing.triggers.Trigger;
import org.apache.flink.streaming.api.windowing.triggers.TriggerResult;
import org.apache.flink.streaming.api.windowing.windows.Window;

import java.text.DateFormat;
import java.util.Date;

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
        long nowProcessTime = ctx.getCurrentProcessingTime();
        ctx.registerProcessingTimeTimer(nowProcessTime + timeOutProcessTime);
        ValueState<Long> partitionedState = ctx.getPartitionedState(lastTempDescriptor);
        partitionedState.update(nowProcessTime);
        log.info("注册一个处理时间触发器，触发时间:{}",DateUtil.format(new Date(nowProcessTime + timeOutProcessTime),"yyyy-mm-dd HH:mm:ss"));
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
        ValueState<Long> partitionedState = ctx.getPartitionedState(lastTempDescriptor);
        Long lastProcessTime = partitionedState.value();
        if (lastProcessTime != null) {
            long diffTime = nowProcessTime - lastProcessTime;
            if (diffTime >= timeOutProcessTime) {
                log.info("当前的处理时间：{},上一次的数据处理时间：{},diffTime:{}",
                        DateUtil.format(new Date(nowProcessTime), "yyyy-mm-dd HH:mm:ss"),
                        DateUtil.format(new Date(lastProcessTime), "yyyy-mm-dd HH:mm:ss"),
                        diffTime
                );
                return fireAndPurge(window, ctx);
            }
        }
        if (ctx.getCurrentWatermark() < window.maxTimestamp()) {
            return TriggerResult.CONTINUE;
        } else {
            log.debug("fire with process tiem: {}", ctx.getCurrentWatermark() );
            return fireAndPurge(window, ctx);
        }
    }

    @Override
    public TriggerResult onEventTime(long time, Window window, TriggerContext ctx) throws Exception {
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
