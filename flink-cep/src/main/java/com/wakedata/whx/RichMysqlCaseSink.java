package com.wakedata.whx;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;

/**
 * TODO
 *
 * @author: WangHuXiong
 * @title: RichMysqlCaseSink
 * @date: 2020-12-26 20:57
 * @program: FlinkStudy
 * @description:
 **/
@Slf4j
public class RichMysqlCaseSink extends RichSinkFunction<List<Tuple3<Long, String, Long>>> {

    private UserInfo userInfo;


    public RichMysqlCaseSink(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    private Connection connection = null;

    private String insert_sql;

    private static String SVIP_UPSERT_CASE = "insert into test_svip_table values(%s,'%s',%s)";
    private static String VIP_UPSERT_CASE = "insert into test_vip_table values(%s,'%s',%s)";
    private static String NORMAL_UPSERT_CASE = "insert into test_normal_table values(%s,'%s',%s)";

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        initConnection();
        switch (userInfo) {
            case VIP:
                insert_sql = VIP_UPSERT_CASE;
                initVipTable();
                break;
            case SVIP:
                insert_sql = SVIP_UPSERT_CASE;
                initSVipTable();
                break;
            case NORMAL:
                insert_sql = NORMAL_UPSERT_CASE;
                initNormalTable();
                break;
            default:
                break;
        }
    }

    @Override
    public void invoke(List<Tuple3<Long, String, Long>> value, Context context) throws Exception {
        value.forEach(action -> {
            String format = String.format(insert_sql, action.f0, action.f1, action.f2);
            executeSql(format);
        });
    }

    @Override
    public void close() throws Exception {
        super.close();
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    private void initSVipTable() {
        try {
            executeSql("CREATE TABLE if not EXISTS `test_svip_table` (\n"
                + "  `id` bigint(20) NOT NULL,\n"
                + "  `name` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT NULL,\n"
                + "  ` money` bigint(20) DEFAULT NULL\n"
                + ") ENGINE=InnoDB ");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initVipTable() {
        try {
            executeSql("CREATE TABLE if not EXISTS `test_vip_table` (\n"
                + "  `id` bigint(20) NOT NULL,\n"
                + "  `name` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT NULL,\n"
                + "  ` money` bigint(20) DEFAULT NULL"
                + ") ENGINE=InnoDB ");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initNormalTable() {
        try {
            executeSql("CREATE TABLE if not EXISTS `test_normal_table` (\n"
                + "  `id` bigint(20) NOT NULL,\n"
                + "  `name` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT NULL,\n"
                + "  ` money` bigint(20) DEFAULT NULL\n"
                + ") ENGINE=InnoDB ");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void executeSql(String sql) {
        Statement statement = null;
        try {
            if (connection != null && !connection.isClosed()) {
                statement = connection.createStatement();
                statement.execute(sql);
            }
            System.err.println("execute sql success:" + sql);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("execute sql error:" + sql);
        } finally {
            try {
                if (statement != null && !statement.isClosed()) {
                    statement.close();
                }
            } catch (SQLException sqlException) {
                sqlException.printStackTrace();
            }
        }
    }

    private void initConnection() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        connection = DriverManager
            .getConnection("jdbc:mysql://172.26.59.35:3306/test?user=mysqluser&password=mysqluser");
    }
}
