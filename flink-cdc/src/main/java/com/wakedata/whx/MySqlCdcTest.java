package com.wakedata.whx;

import org.apache.flink.table.api.TableResult;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

/**
 * TODO
 *
 * @author: WangHuXiong
 * @title: MySqlCdcTest
 * @date: 2021-02-20 17:38
 * @program: FlinkStudy
 * @description:
 **/
public class MySqlCdcTest {

    public static void main(String[] args) {
        StreamTableEnvironment tableExecutionEnvironment = EnvironmentUtil
            .getTableExecutionEnvironment();
        tableExecutionEnvironment
            .executeSql("CREATE TABLE mysql_binlog (\n"
                + " id INT NOT NULL,\n"
                + " order_id INT,\n"
                + " sku_id INT,\n"
                + " sku_name STRING,\n"
                + " sku_num INT,\n"
                + " order_price DECIMAL(10,3),\n"
                + " create_time timestamp\n"
                + ") WITH (\n"
                + " 'connector' = 'mysql-cdc',\n"
                + " 'hostname' = '172.31.3.95',\n"
                + " 'port' = '3307',\n"
                + " 'username' = 'root',\n"
                + " 'password' = '123456',\n"
                + " 'database-name' = 'stream',\n"
                + " 'table-name' = 'order_detail'\n"
                + ")");
        TableResult tableResult = tableExecutionEnvironment
            .executeSql("select * from mysql_binlog");
        tableResult.print();
    }

}
