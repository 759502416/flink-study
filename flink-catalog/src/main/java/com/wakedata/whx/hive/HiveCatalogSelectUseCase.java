package com.wakedata.whx.hive;

import com.wakedata.whx.EnvironmentUtil;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.table.catalog.exceptions.TableNotExistException;
import org.apache.flink.table.catalog.hive.HiveCatalog;
import org.apache.hadoop.hive.conf.HiveConf;

/**
 * TODO
 *
 * @author: WangHuXiong
 * @title: HiveCatalogUseTmp
 * @date: 2021-01-25 19:49
 * @program: FlinkStudy
 * @description:
 **/
class HiveCatalogSelectUseCase {

    public static void main(String[] args) {
        StreamTableEnvironment tableEnv = EnvironmentUtil
            .getTableExecutionEnvironment();
        System.setProperty("HADOOP_USER_NAME", "hive");
        HiveCatalog catalog = new HiveCatalog("myhive", null, "E:\\etc\\hive\\conf");
        tableEnv.registerCatalog("myhive", catalog);
        tableEnv.executeSql("select * from myhive.test.test1").print();
    }

}
