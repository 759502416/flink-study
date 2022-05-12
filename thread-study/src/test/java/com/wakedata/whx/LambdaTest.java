package com.wakedata.whx;

import org.junit.Test;

/**
 * @author wanghx
 * @describe
 * @since 2022/3/23 11:36
 */
public class LambdaTest {

    @Test
    public void testLambda() {
        Call call1 = new Call() {
            @Override
            public int add(int a, int b) {
                return a + b;
            }
        };
        Call c = (int a, int b) -> {
            return a + b;
        };

    }


}
