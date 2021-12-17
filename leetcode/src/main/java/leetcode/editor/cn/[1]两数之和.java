package leetcode.editor.cn;

import java.util.HashMap;

class question1 {
//给定一个整数数组 nums 和一个整数目标值 target，请你在该数组中找出 和为目标值 target 的那 两个 整数，并返回它们的数组下标。 
//
// 你可以假设每种输入只会对应一个答案。但是，数组中同一个元素在答案里不能重复出现。 
//
// 你可以按任意顺序返回答案。 
//
// 
//
// 示例 1： 
//
// 
//输入：nums = [2,7,11,15], target = 9
//输出：[0,1]
//解释：因为 nums[0] + nums[1] == 9 ，返回 [0, 1] 。
// 
//
// 示例 2： 
//
// 
//输入：nums = [3,2,4], target = 6
//输出：[1,2]
// 
//
// 示例 3： 
//
// 
//输入：nums = [3,3], target = 6
//输出：[0,1]
// 
//
// 
//
// 提示： 
//
// 
// 2 <= nums.length <= 10⁴ 
// -10⁹ <= nums[i] <= 10⁹ 
// -10⁹ <= target <= 10⁹ 
// 只会存在一个有效答案 
// 
//
// 进阶：你可以想出一个时间复杂度小于 O(n²) 的算法吗？ 
// Related Topics 数组 哈希表 👍 12110 👎 0

    /**
     * 解题思路：
     * 1、创建一个 kv对存储数据，key为数据，value为index（因为需要返回的就是index）
     * 2、遍历数组，存储到kv对，并查询kv对的key是否包含我们寻找的数据。
     */

    //leetcode submit region begin(Prohibit modification and deletion)
    class Solution {
        public int[] twoSum(int[] nums, int target) {
            HashMap<Integer, Integer> numIndexMap = new HashMap<>(nums.length);
            for (int i = 0; i < nums.length; i++) {
                int num1 = nums[i];
                int needNum = target - num1;
                if (numIndexMap.containsKey(needNum)) {
                    return new int[]{numIndexMap.get(needNum), i};
                }
                numIndexMap.put(num1, i);
            }
            return new int[]{};
        }
    }
//leetcode submit region end(Prohibit modification and deletion)

}

