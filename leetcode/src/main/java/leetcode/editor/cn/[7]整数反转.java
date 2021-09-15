package leetcode.editor.cn;

public class question7 {
//给你一个 32 位的有符号整数 x ，返回将 x 中的数字部分反转后的结果。 
//
// 如果反转后整数超过 32 位的有符号整数的范围 [−2³¹, 231 − 1] ，就返回 0。 
//假设环境不允许存储 64 位整数（有符号或无符号）。
//
// 
//
// 示例 1： 
//
// 
//输入：x = 123
//输出：321
// 
//
// 示例 2： 
//
// 
//输入：x = -123
//输出：-321
// 
//
// 示例 3： 
//
// 
//输入：x = 120
//输出：21
// 
//
// 示例 4： 
//
// 
//输入：x = 0
//输出：0
// 
//
// 
//
// 提示： 
//
// 
// -2³¹ <= x <= 2³¹ - 1 
// 
// Related Topics 数学 👍 3074 👎 0


    //leetcode submit region begin(Prohibit modification and deletion)
    class Solution {
        public int reverse(int x) {
            int finalNum = 0;
            int maxValue = Integer.MAX_VALUE / 10;
            int minValue = Integer.MIN_VALUE / 10;
            while (x != 0) {
                finalNum = finalNum * 10 + x % 10;
                x = x / 10;
                if (x != 0 && (finalNum > maxValue || (finalNum == maxValue && x > 7))) {
                    return 0;
                }
                if (x != 0 && (finalNum < minValue || (finalNum == minValue && x < -8))) {
                    return 0;
                }
            }
            return finalNum;
        }
    }
//leetcode submit region end(Prohibit modification and deletion)

}

