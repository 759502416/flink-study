package com.wakedata.whx.mysqlcdc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

/**
 * @author: WangHuXiong
 * @title: MysqlBacthChangeUtil
 * @date: 2020-09-28 17:00
 * @program: FlinkStudy
 * @description:
 **/
@Slf4j
public class MysqlBatchChangeUtil {

    /**
     * mysql的连接对象
     */
    private static Connection mySqlConnection;


    /**
     * 获得mysql的连接器
     *
     * @param url
     * @param driver
     * @param name
     * @param passWord
     * @return
     */
    public static void getMysqlConnection(String url, String driver, String name,
        String passWord) {

        try {
            Class.forName(driver);
            mySqlConnection = DriverManager.getConnection(url, name, passWord);
            log.info("成功获得mysql Connection");
        } catch (SQLException | ClassNotFoundException sqlException) {
            log.error("获得mysql Connection 发生异常");
            sqlException.printStackTrace();
        }
    }

    /**
     * 执行mysql的操作
     */
    public static ResultSet executeMysqlAction(String sqlString, SqlActionEnum sqlActionEnum)
        throws SQLException {
        Statement statement = mySqlConnection.createStatement();
        if (SqlActionEnum.SELECT == sqlActionEnum) {
            return statement.executeQuery(sqlString);
        } else {
            statement.execute(sqlString);
            return null;
        }
    }

    public static void mysqlConnectionClose() {
        try {
            if (mySqlConnection != null && !mySqlConnection.isClosed()) {
                mySqlConnection.close();
                log.info("关闭mysql连接");
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }


    @Test
    public void testWriteMysql() {
        try {
            String url = "jdbc:mysql://47.75.93.78:3306/test?autoReconnect=true&useSSL=false";
            String driver = "com.mysql.jdbc.Driver";
            String userName = "root";
            String passWord = "XreaServer_2014";
            getMysqlConnection(url, driver, userName, passWord);
            String nowTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

            Long id = 1L;
            String name = "whx";

            /***sql 字串拼接开始**/
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("insert into test_cdc_mysql values(");
            stringBuffer.append(id);
            stringBuffer.append(",'");
            stringBuffer.append(name);
            stringBuffer.append("','");
            stringBuffer.append(nowTime);
            stringBuffer.append("')");
            /***sql 字串拼接结束**/
            log.info("执行sql语句：{}", stringBuffer.toString());
            MysqlBatchChangeUtil.executeMysqlAction(stringBuffer.toString(), SqlActionEnum.INSERT);
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        } finally {
            mysqlConnectionClose();
        }
    }
}
