package com.wakedata.learn;

import com.wakedata.whx.EnvironmentUtil;
import org.apache.flink.table.api.TableResult;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

/**
 *  cep 股票表测试
 * @author: WangHuXiong
 * @title: CepStockTableTest
 * @date: 2021-01-14 11:51
 * @program: FlinkStudy
 * @description:
 **/
public class CepStockTableTest {
    public static void main(String[] args) {
        // 开启checkPoint
        EnvironmentUtil.turnOnCheckpoint();
        // 平台自动化获取flink连接属性
        StreamTableEnvironment tableExecutionEnvironment = EnvironmentUtil
            .getTableExecutionEnvironment();
        tableExecutionEnvironment.executeSql("CREATE TABLE Ticker (\n"
            + "   `symbol` STRING,\n"
            + "  `price` INT,\n"
            + "  `tax` INT,\n"
            + "  `rowtime TIMESTAMP,\n"
            + "  `WATERMARK  wk1 FOR rowtime as withOffset(rowtime,1000),\n"
            + ") WITH (\n"
            + "  'connector' = 'kafka',\n"
            + "  'topic' = 'Ticker',\n"
            + "  'properties.bootstrap.servers' = 'hd-node-3-41.wakedata.com:6667,hd-node-3-42.wakedata.com:6667,hd-node-3-43.wakedata.com:6667',\n"
            + "  'properties.group.id' = 'test_cep',\n"
            + "  'scan.startup.mode' = 'earliest-offset',\n"
            + "  'format' = 'json'\n"
            + ")");
        TableResult tableResult = tableExecutionEnvironment
            .executeSql("");
        tableResult.print();

    }
}
