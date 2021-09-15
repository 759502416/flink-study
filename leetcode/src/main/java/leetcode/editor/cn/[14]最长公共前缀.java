package leetcode.editor.cn;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

class question14 {
//编写一个函数来查找字符串数组中的最长公共前缀。 
//
// 如果不存在公共前缀，返回空字符串 ""。 
//
// 
//
// 示例 1： 
//
// 
//输入：strs = ["flower","flow","flight"]
//输出："fl"
// 
//
// 示例 2： 
//
// 
//输入：strs = ["dog","racecar","car"]
//输出：""
//解释：输入不存在公共前缀。 
//
// 
//
// 提示： 
//
// 
// 1 <= strs.length <= 200 
// 0 <= strs[i].length <= 200 
// strs[i] 仅由小写英文字母组成 
// 
// Related Topics 字符串 👍 1778 👎 0


    //leetcode submit region begin(Prohibit modification and deletion)
    class Solution {
        public String longestCommonPrefix(String[] strs) {
            String minStr = strs[0];
            int maxIndex = minStr.length();
            char[] characters = minStr.toCharArray();
            for (int i = 0; i < strs.length; i++) {
                String str = strs[i];
                int min = Math.min(str.length(), maxIndex);
                if (min == 0) {
                    return "";
                }
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
//leetcode submit region end(Prohibit modification and deletion)

}

