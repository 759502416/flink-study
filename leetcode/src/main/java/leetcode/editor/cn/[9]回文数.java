package leetcode.editor.cn;

class question9 {
//给你一个整数 x ，如果 x 是一个回文整数，返回 true ；否则，返回 false 。 
//
// 回文数是指正序（从左向右）和倒序（从右向左）读都是一样的整数。例如，121 是回文，而 123 不是。 
//
// 
//
// 示例 1： 
//
// 
//输入：x = 121
//输出：true
// 
//
// 示例 2： 
//
// 
//输入：x = -121
//输出：false
//解释：从左向右读, 为 -121 。 从右向左读, 为 121- 。因此它不是一个回文数。
// 
//
// 示例 3： 
//
// 
//输入：x = 10
//输出：false
//解释：从右向左读, 为 01 。因此它不是一个回文数。
// 
//
// 示例 4： 
//
// 
//输入：x = -101
//输出：false
// 
//
// 
//
// 提示： 
//
// 
// -2³¹ <= x <= 2³¹ - 1 
// 
//
// 
//
// 进阶：你能不将整数转为字符串来解决这个问题吗？ 
// Related Topics 数学 👍 1616 👎 0


    //leetcode submit region begin(Prohibit modification and deletion)
    class Solution {
        public boolean isPalindrome(int x) {
            if (x < 0) {
                return false;
            }
            if (x %10 ==0 && x !=0) {
                return false;
            }
            if (x < 10) {
                return true;
            }
            int ori = x;
            int finalNum = 0;
            int maxValue = Integer.MAX_VALUE / 10;
            while (x != 0) {
                // 取尾
                finalNum = finalNum * 10 + x % 10;
                // 取头
                x = x / 10;
                if (finalNum == x) {
                    return true;
                }
                if (x != 0 && (finalNum > maxValue || (finalNum == maxValue && x > 7))) {
                    return false;
                }
            }
            return finalNum == ori;
        }

        public boolean isPalindromeToStr(int x) {
            if (x < 0) {
                return false;
            }
            if (x < 10) {
                return true;
            }
            char[] charArray = String.valueOf(x).toCharArray();
            int length = charArray.length;
            for (int i = 0; i < length / 2 + 1; i++) {
                if (charArray[i] != charArray[length - i - 1]) {
                    return false;
                }
            }
            return true;
        }
    }
//leetcode submit region end(Prohibit modification and deletion)

}

