package com.wakedata.whx.mysqlcdc;

import java.util.concurrent.ExecutionException;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.JobExecutionResult;
import org.apache.flink.connector.jdbc.dialect.MySQLDialect;
import org.apache.flink.connector.jdbc.internal.options.JdbcOptions;
import org.apache.flink.connector.jdbc.table.JdbcUpsertTableSink;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.TableSchema;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.types.Row;
import org.apache.flink.util.CloseableIterator;
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
        StringBuffer debeziumKafkaSource = new StringBuffer();
        debeziumKafkaSource.append("CREATE TABLE test_cdc_mysql (");
        debeziumKafkaSource.append("  id BIGINT,");
        debeziumKafkaSource.append("  name VARCHAR,");
        debeziumKafkaSource.append("  birthday TIMESTAMP");
        debeziumKafkaSource.append(") WITH (");
        debeziumKafkaSource.append(" 'connector' = 'kafka',");
        debeziumKafkaSource.append(" 'topic' = 'whx.test.test_cdc_mysql',");
        debeziumKafkaSource.append(
            " 'properties.bootstrap.servers' = 'hd-node-3-41.wakedata.com:6667,hd-node-3-42.wakedata.com:6667,hd-node-3-43.wakedata.com:6667',");
        debeziumKafkaSource.append(" 'properties.group.id' = 'testGroup4',");
        debeziumKafkaSource.append(" 'debezium-json.schema-include' = 'true',");
        debeziumKafkaSource.append(" 'debezium-json.timestamp-format.standard' = 'ISO-8601',");
        debeziumKafkaSource.append(" 'format' = 'debezium-json' )");
        streamTableEnv.executeSql(debeziumKafkaSource.toString());

        Table table = streamTableEnv.sqlQuery("select * from test_cdc_mysql");
        /***
         * SINK Start
         */
        JdbcOptions jdbcOptions = JdbcOptions.builder()
            .setDBUrl("jdbc:mysql://47.75.93.78:3306/test")
            .setTableName("test.test_cdc_mysql_upser")
            .setUsername("root")
            .setPassword("XreaServer_2014")
            .setDialect(new MySQLDialect())
            .setDriverName("com.mysql.jdbc.Driver")
            .build();
        JdbcUpsertTableSink jdbcUpsertTableSink = JdbcUpsertTableSink.builder()
            .setTableSchema(table.getSchema())
            .setOptions(jdbcOptions)
            .build();
        jdbcUpsertTableSink.setKeyFields(new String[]{"id"});
        /**
         * SINK End
         */
        streamTableEnv.registerTableSink("upsert_mysql_table",jdbcUpsertTableSink);
        try {
            JobExecutionResult jobExecutionResult = streamTableEnv
                .executeSql("insert into upsert_mysql_table \n"
                    + "select id,last_value(name) as name ,max(birthday) as birthday from test_cdc_mysql group by id")
                .getJobClient()
                .get()
                .getJobExecutionResult(Thread.currentThread().getContextClassLoader())
                .get();
            System.err.println(jobExecutionResult.toString());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }


}
