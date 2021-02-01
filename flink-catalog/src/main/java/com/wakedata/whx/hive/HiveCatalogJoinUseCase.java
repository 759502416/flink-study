package com.wakedata.whx.hive;

import com.wakedata.whx.EnvironmentUtil;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.table.catalog.exceptions.TableNotExistException;
import org.apache.flink.table.catalog.hive.HiveCatalog;

/**
 * TODO
 *
 * @author: WangHuXiong
 * @title: HiveCatalogUpdateUseCase
 * @date: 2021-02-01 16:16
 * @program: FlinkStudy
 * @description:
 **/
public class HiveCatalogJoinUseCase {
    public static void main(String[] args) throws TableNotExistException {
        StreamTableEnvironment tableEnv = EnvironmentUtil
            .getTableExecutionEnvironment();
        System.setProperty("HADOOP_USER_NAME", "hive");
        HiveCatalog catalog = new HiveCatalog("myhive", null, "E:\\etc\\hive\\conf");
        tableEnv.registerCatalog("myhive", catalog);
        tableEnv.executeSql("select t1.id,t1.name,t1.age from myhive.test.test1 t1\n"
            + "inner join myhive.test.test11 t2 on t2.name = t1.id").print();
    }
}
