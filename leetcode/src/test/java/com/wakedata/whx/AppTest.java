package com.wakedata.whx;

import org.junit.Test;

/**
 * Unit test for simple App.
 */
public class AppTest {
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue() {
        String[] strings = {"flower","flow","flight"};
        System.err.println(longestCommonPrefix(strings));
    }

    public String longestCommonPrefix(String[] strs) {
        String minStr = strs[0];
        int maxIndex = minStr.length();
        char[] characters = minStr.toCharArray();
        for (int i = 0; i < strs.length; i++) {
            String str = strs[i];
            int min = Math.min(str.length(), maxIndex);
            for (int j = 0; j < min; j++) {
                if (characters[j] != str.charAt(j)) {
                    maxIndex = j;
                    break;
                }
                maxIndex = j + 1;
            }
        }
        String result = "";
        for (int i = 0; i < maxIndex; i++) {
            result += characters[i];
        }
        return result;
    }
}
