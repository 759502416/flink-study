package com.wakedata.whx.datastream.watermark;

import com.wakedata.whx.datastream.util.StringToLongTimeUtil;
import org.apache.flink.api.common.eventtime.*;
import org.apache.flink.api.java.tuple.Tuple3;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author :wanghuxiong
 * @title: MyWatermarkStrategy
 * @projectName flink-study
 * @description: TODO
 * @date 2020/11/10 12:29 上午
 */
public class MyWatermarkStrategy implements WatermarkStrategy<Tuple3<String, String, Long>> {

    private long maxTimestamp = 0L;
    // 延迟毫秒数
    private long delay = 1000L;

    @Override
    public WatermarkGenerator<Tuple3<String, String, Long>> createWatermarkGenerator(WatermarkGeneratorSupplier.Context context) {
        return new WatermarkGenerator<Tuple3<String, String, Long>>() {
            @Override
            public void onEvent(Tuple3<String, String, Long> stringStringLongTuple3, long l, WatermarkOutput watermarkOutput) {
                maxTimestamp = Math.max(l, maxTimestamp);
            }

            @Override
            public void onPeriodicEmit(WatermarkOutput watermarkOutput) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                System.err.println(Thread.currentThread().getId()+"发射水位线："+ simpleDateFormat.format(new Date(maxTimestamp)));
                watermarkOutput.emitWatermark(new Watermark(maxTimestamp - delay));
            }
        };
    }

    @Override
    public TimestampAssigner<Tuple3<String, String, Long>> createTimestampAssigner(TimestampAssignerSupplier.Context context) {
        return new TimestampAssigner<Tuple3<String, String, Long>>() {
            @Override
            public long extractTimestamp(Tuple3<String, String, Long> stringStringLongTuple3, long l) {
                return StringToLongTimeUtil
                        .getTimeStampFromDateString(stringStringLongTuple3.f1);
            }
        };
    }
}
