package com.wakedata.whx.hive;

import com.wakedata.whx.EnvironmentUtil;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.table.catalog.hive.HiveCatalog;

/**
 * TODO
 *
 * @author: WangHuXiong
 * @title: HiveCatalogInsertUseCase
 * @date: 2021-02-01 17:30
 * @program: FlinkStudy
 * @description:
 **/
public class HiveCatalogInsertUseCase {

    public static void main(String[] args) {
        StreamTableEnvironment tableEnv = EnvironmentUtil
            .getTableExecutionEnvironment();
        System.setProperty("HADOOP_USER_NAME", "hive");
        HiveCatalog catalog = new HiveCatalog("myhive", null, "E:\\etc\\hive\\conf");
        tableEnv.registerCatalog("myhive", catalog);
        tableEnv.executeSql("insert into myhive.test.test1 values(8,'whx8',8,'home8')");
    }

}
