package com.wakedata.whx;

import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.TableEnvironment;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

/**
 * Hello world!
 */
public class App {

    public static void main(String[] args) {
        EnvironmentSettings environmentSettings = EnvironmentSettings.newInstance().inBatchMode().useBlinkPlanner().build();
        TableEnvironment tenv = TableEnvironment.create(environmentSettings);
        System.setProperty("HADOOP_USER_NAME", "hive");
        tenv.executeSql("CREATE CATALOG iceberg WITH (\n" +
            "  'type'='iceberg',\n" +
            "  'catalog-type'='hive'," +
            "  'uri'='thrift://hd-node-3-41.wakedata.com:9083,thrift://hd-node-3-42.wakedata.com:9083,thrift://wake-sz-vm-3-94.wakedata.com:9083', "
            +
            "  'clients'='5', " +
            "  'property-version'='1', " +
            "  'warehouse'='hdfs://hd-node-3-24.wakedata.com:8020/warehouse'" +
            ")");

        tenv.useCatalog("iceberg");
        /*tenv.executeSql("CREATE DATABASE iceberg_db_whx");
        tenv.useDatabase("iceberg_db_whx");*/
        tenv.useDatabase("iceberg_db");
       /* tenv.executeSql(" DROP TABLE IF EXISTS iceberg.iceberg_db.sourcetable");
        tenv.executeSql("CREATE TABLE iceberg.iceberg_db.sourcetable (\n" +
            " userid int,\n" +
            " f_random_str STRING\n" +
            ")");
*/
        //tenv.executeSql(
        //    "insert into iceberg.iceberg_db.iceberg_001 select * from iceberg.iceberg_db.sourceTable");

        tenv.executeSql("show tables").print();
        //   tenv.executeSql("show databases").print();
        tenv.executeSql("insert into iceberg.iceberg_db.sourcetable values(1,'whx')");
        tenv.executeSql("insert into iceberg.iceberg_db.sourcetable values(2,'wc')");


        tenv.executeSql("select * from iceberg.iceberg_db.sourcetable").print();
    }
}
