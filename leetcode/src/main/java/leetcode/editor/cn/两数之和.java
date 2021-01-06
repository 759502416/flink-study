package leetcode.editor.cn;

//给定一个整数数组 nums 和一个整数目标值 target，请你在该数组中找出 和为目标值 的那 两个 整数，并返回它们的数组下标。 
//
// 你可以假设每种输入只会对应一个答案。但是，数组中同一个元素不能使用两遍。 
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
// 2 <= nums.length <= 103 
// -109 <= nums[i] <= 109 
// -109 <= target <= 109 
// 只会存在一个有效答案 
// 
// Related Topics 数组 哈希表 
// 👍 9985 👎 0

import java.util.Arrays;
import java.util.HashMap;

public class 两数之和{
	public static void main(String[] args) {
		Solution solution = new 两数之和().new Solution();
		int[] ints = solution.twoSum(new int[]{1, 2, 3}, 3);
		Arrays.stream(ints).asLongStream().forEach(x -> System.out.println(x));

	}
//leetcode submit region begin(Prohibit modification and deletion)
class Solution {
    public int[] twoSum(int[] nums, int target) {
		HashMap<Integer, Integer> integerIntegerHashMap = new HashMap<>(nums.length);
		for (int i = 0; i < nums.length; i++) {
			int num = nums[i];
			int diff = target - num;
			if(integerIntegerHashMap.containsKey(diff)){
				return new int[]{i,integerIntegerHashMap.get(diff)};
			}
			integerIntegerHashMap.put(num,i);
		}
		return new int[]{};
	}
}
//leetcode submit region end(Prohibit modification and deletion)
}