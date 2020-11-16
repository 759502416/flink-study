package com.wakedata.whx.datastream.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * @author :wanghuxiong
 * @title: StringToLongTimeUtil
 * @projectName flink-study
 * @description: TODO
 * @date 2020/11/8 11:46 下午
 */
public class StringToLongTimeUtil {

    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    public static Long getTimeStampFromDateString(String dateString) {
        return getTimeStampFromDateString(dateString, Long.MIN_VALUE);
    }

    public static synchronized Long getTimeStampFromDateString(String dateString, Long defaultDateString) {
        try {
            System.err.println("daString:" + dateString);
            return simpleDateFormat.parse(dateString).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return defaultDateString;
        }
    }


}
