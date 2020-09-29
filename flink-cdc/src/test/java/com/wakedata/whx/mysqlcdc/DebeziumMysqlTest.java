package com.wakedata.whx.mysqlcdc;

import lombok.extern.slf4j.Slf4j;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.junit.Before;
import org.junit.Test;

/**
 * TODO
 *
 * @author: WangHuXiong
 * @title: DebeziumMysqlTest
 * @date: 2020-09-29 20:45
 * @program: FlinkStudy
 * @description:
 **/
@Slf4j
public class DebeziumMysqlTest {

    /**
     * 流式处理对象
     */
    private static StreamExecutionEnvironment streamEnv = null;

    /**
     * 流式处理table对象
     */
    private static StreamTableEnvironment streamTableEnv = null;

    @Before
    public void initEnv() {
        streamEnv = StreamExecutionEnvironment
            .getExecutionEnvironment();
        streamTableEnv = StreamTableEnvironment.create(streamEnv);
    }

    @Test
    public void testInitDataToDebezium() {

    }


}
