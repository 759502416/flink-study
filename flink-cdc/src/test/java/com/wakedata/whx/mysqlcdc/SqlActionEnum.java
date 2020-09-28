package com.wakedata.whx.mysqlcdc;

/**
 * 数据库的操作方式
 * @author: WangHuXiong
 * @title: SqlActionEnum
 * @date: 2020-09-28 17:31
 * @program: FlinkStudy
 * @description:
 **/
public enum SqlActionEnum {
    /**
     * 数据库的增删改查操作
     */
    UPDATE("update"),
    INSERT("insert"),
    DELETE("delete"),
    SELECT("select");
    /**
     * 动作前缀
     */
    private String actionPrefix;


    SqlActionEnum(String actionPrefix) {
        this.actionPrefix = actionPrefix;
    }

    public String getActionPrefix() {
        return actionPrefix;
    }
}
