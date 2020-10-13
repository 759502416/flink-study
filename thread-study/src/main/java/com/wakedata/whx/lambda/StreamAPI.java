package com.wakedata.whx.lambda;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author :wanghuxiong
 * @title: StreamAPi
 * @projectName flink-study
 * @description: TODO
 * @date 2020/10/14 12:20 上午
 */
public class StreamAPI {

    public static void main(String[] args) {
        List<String> nameStrs = Arrays.asList("Monkey", "Lion", "Giraffe", "Lemur");
        List<String> list = nameStrs.stream()
                .filter(s -> s.startsWith("L"))
                .map(String::toLowerCase)
                .sorted()
                .collect(Collectors.toList());
        list.stream().forEach(data -> System.err.println(data));

        System.err.println("-------------------------------");
        // 数组转换为流
        String[] players = {"kobe", "james", "curry", "cyyt"};
        Stream.of(players)
                .filter(str -> str.length() > 4)
                .map(str -> new StringBuffer(str).append("**").toString())
                .sorted()
                .collect(Collectors.toList())
                .forEach(data -> System.err.println(data));
    }
}
