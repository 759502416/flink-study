package com.wakedata.learn;

import com.wakedata.whx.EnvironmentUtil;
import org.apache.flink.table.api.TableResult;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

/**
 * TODO
 *
 * @author: WangHuXiong
 * @title: CepTableTest
 * @date: 2021-01-14 10:18
 * @program: FlinkStudy
 * @description:
 **/
public class CepTableTest {

    public static void main(String[] args) {
        // 开启checkPoint
        EnvironmentUtil.turnOnCheckpoint();
        // 平台自动化获取flink连接属性
        StreamTableEnvironment tableExecutionEnvironment = EnvironmentUtil
            .getTableExecutionEnvironment();
        tableExecutionEnvironment.executeSql("CREATE TABLE whx_cep_test_topic3 (\n"
            + "  `id` INT,\n"
            + "  `num` INT,\n"
            + "  `name` STRING,\n"
            + "  `user_action_time` AS PROCTIME()\n"
            + ") WITH (\n"
            + "  'connector' = 'kafka',\n"
            + "  'topic' = 'whx_cep_test_topic3',\n"
            + "  'properties.bootstrap.servers' = 'hd-node-3-41.wakedata.com:6667,hd-node-3-42.wakedata.com:6667,hd-node-3-43.wakedata.com:6667',\n"
            + "  'properties.group.id' = 'test_cep',\n"
            + "  'scan.startup.mode' = 'earliest-offset',\n"
            + "  'format' = 'json'\n"
            + ")");
        TableResult tableResult = tableExecutionEnvironment
            .executeSql("select T.begname,T.onename,T.twoname from whx_cep_test_topic3 \n"
                + " MATCH_RECOGNIZE (\n"
                + "\tPARTITION BY id\n"
                + "\tORDER BY user_action_time\n"
                + "\tMEASURES\n"
                + "\t\t`beg`.name as begname,\n"
                + "\t\t`one`.name as onename,\n"
                + "\t\t`two`.name as twoname\n"
                + "\tPATTERN (`beg`  `one`  `two`)\n"
                + "\tDEFINE\n"
                + "\t\t`beg` AS num = 0,\n"
                + "\t\t`one` AS num = 1,\n"
                + "\t\t`two` AS num = 2\n"
                + " ) AS T");
        tableResult.print();

    }
}
