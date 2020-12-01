package com.wakedata.whx.flinksql;

import com.wakedata.whx.EnvironmentUtil;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;

import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.api.java.typeutils.TupleTypeInfo;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.table.api.DataTypes;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.TableSchema;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.table.types.DataType;
import org.junit.Test;

/**
 * @author :wanghuxiong
 * @title: FirstTest
 * @projectName flink-study
 * @description: TODO
 * @date 2020/12/1 10:57 下午
 */
public class FirstTest {

    @Test
    public void testTable() {
        // 获得 tableEnvironment
        StreamTableEnvironment tableEnvironment = EnvironmentUtil.getTableExecutionEnvironment();

       // DataType row = DataTypes.ROW(DataTypes.FIELD("name", DataTypes.STRING()), DataTypes.FIELD("onTime", DataTypes.DATE()), DataTypes.FIELD("number", DataTypes.BIGINT()));
        //Table table = tableEnvironment.fromValues(TupleTypeInfo.of(new TypeHint<Tuple3<String, String, Long>>() {}), EnvironmentUtil.tuple3StringStringLongElements);
        DataStreamSource<Tuple3<String, String, Long>> tuple3DataStreamSource = EnvironmentUtil.getStreamExecutionEnvironment().fromCollection(EnvironmentUtil.tuple3StringStringLongElements);
        Table table = tableEnvironment.fromDataStream(tuple3DataStreamSource);

        TableSchema schema = table.getSchema();
        table.printSchema();

    }
}
