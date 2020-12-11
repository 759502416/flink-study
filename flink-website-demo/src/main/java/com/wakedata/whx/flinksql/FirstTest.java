package com.wakedata.whx.flinksql;

import com.wakedata.whx.EnvironmentUtil;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.TimeZone;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.apache.flink.table.api.Over;
import org.apache.flink.table.api.OverWindowedTable;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.Tumble;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.types.Row;
import org.junit.Test;

import java.time.ZoneId;

import static org.apache.flink.table.api.Expressions.$;
import static org.apache.flink.table.api.Expressions.lit;

/**
 * @author :wanghuxiong
 * @title: FirstTest
 * @projectName flink-study
 * @description: TODO
 * @date 2020/12/1 10:57 下午
 */
public class FirstTest {

    // the number of milliseconds in a day
    private static final long MILLIS_PER_DAY = 86400000;

    public LocalDateTime toLocalDateTime(Long millisecond) {
        int date = (int) (millisecond / MILLIS_PER_DAY);
        int time = (int) (millisecond % MILLIS_PER_DAY);
        if (time < 0) {
            --date;
            time += MILLIS_PER_DAY;
        }
        long nanoOfDay = time * 1_000_000L + 0;
        LocalDate localDate = LocalDate.ofEpochDay(date);
        LocalTime localTime = LocalTime.ofNanoOfDay(nanoOfDay);
        return LocalDateTime.of(localDate, localTime);
    }

    @Test
    public void test02() {
        System.err.println(TimeZone.getDefault());
        System.err.println(System.currentTimeMillis());
        LocalDateTime localDateTime = toLocalDateTime(System.currentTimeMillis());
        LocalDateTime now = LocalDateTime.now();
        System.err.println(localDateTime.atZone(ZoneId.of(TimeZone.getDefault().getID())));
    }

    @Test
    public void testTable() {
        // 获得 tableEnvironment
        StreamTableEnvironment tableEnvironment = EnvironmentUtil.getTableExecutionEnvironment();
        DataStreamSource<Tuple3<String, String, Long>> tuple3DataStreamSource = EnvironmentUtil.getStreamExecutionEnvironment().fromCollection(EnvironmentUtil.tuple3StringStringLongElements);
        Table table = tableEnvironment.fromDataStream(tuple3DataStreamSource);
        tableEnvironment.createTemporaryView("templeTable", table);

        Table table1 = tableEnvironment.sqlQuery("select * from templeTable");

        DataStream<Row> rowDataStream = tableEnvironment.toAppendStream(table1, Row.class);
        rowDataStream.addSink(new SinkFunction<Row>() {
            @Override
            public void invoke(Row value, Context context) throws Exception {
                System.err.println(value);
            }
        });
        rowDataStream.addSink(new SinkFunction<Row>() {
            @Override
            public void invoke(Row value, Context context) throws Exception {
                System.out.println(value);
            }
        });
        try {
            EnvironmentUtil.getStreamExecutionEnvironment().execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testProcesstime() {
        StreamTableEnvironment tableEnvironment = EnvironmentUtil.getTableExecutionEnvironment();
        DataStreamSource<Tuple3<String, String, Long>> tuple3DataStreamSource = EnvironmentUtil.getStreamExecutionEnvironment().fromCollection(EnvironmentUtil.tuple3StringStringLongElements);
        Table table = tableEnvironment.fromDataStream(tuple3DataStreamSource, $("f0").as("name"), $("f1").as("birthday")
                , $("f2").as("numb"),$("proc_time").proctime());

       // table.window(Tumble.over(lit(10).minutes()).on($("proc_time")).as("userWindow"))

       // OverWindowedTable window = table.window(Over.partitionBy($("c")).orderBy($("rowTime")).preceding(lit(10).seconds()).as("ow"));
        tableEnvironment.registerTable("tmptable",table);
        tableEnvironment.executeSql("select proc_time from tmptable").print();
        System.err.println(System.currentTimeMillis());
    }
}
