package com.wakedata.whx.hive;

import com.wakedata.whx.EnvironmentUtil;
import java.util.HashMap;
import org.apache.flink.table.api.DataTypes;
import org.apache.flink.table.api.TableSchema;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.table.catalog.CatalogBaseTable;
import org.apache.flink.table.catalog.CatalogTableImpl;
import org.apache.flink.table.catalog.ObjectPath;
import org.apache.flink.table.catalog.exceptions.DatabaseNotExistException;
import org.apache.flink.table.catalog.exceptions.TableAlreadyExistException;
import org.apache.flink.table.catalog.hive.HiveCatalog;
import org.apache.hadoop.hive.ql.metadata.Hive;

/**
 * TODO
 *
 * @author: WangHuXiong
 * @title: HiveCatalogPartitionUseCase
 * @date: 2021-02-01 17:38
 * @program: FlinkStudy
 * @description:
 **/
public class HiveCatalogPartitionUseCase {

    public static void main(String[] args)
        throws TableAlreadyExistException, DatabaseNotExistException {
        StreamTableEnvironment tableEnv = EnvironmentUtil
            .getTableExecutionEnvironment();
        System.setProperty("HADOOP_USER_NAME", "hive");
        HiveCatalog catalog = new HiveCatalog("myhive", null, "E:\\etc\\hive\\conf");
        tableEnv.registerCatalog("myhive", catalog);

        tableEnv.executeSql("insert into  myhive.test.whxtest partition(st = 20210201) values(2,'wake2')");
    }

}
