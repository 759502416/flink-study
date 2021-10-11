package com.wakedata.whx.window;

import org.apache.flink.streaming.api.windowing.assigners.SlidingEventTimeWindows;

/**
 * @author :wanghuxiong
 * @title: SlidingProcessTimeWindows
 * @projectName flink-study
 * @description: TODO
 * @date 2021/10/11 5:32 下午
 */
public class SlidingProcessTimeWindows extends SlidingEventTimeWindows {
    protected SlidingProcessTimeWindows(long size, long slide, long offset) {
        super(size, slide, offset);
    }

    @Override
    public boolean isEventTime() {
        return false;
    }
}
