package com.glowxq.oj.problem.pojo.vo;

import com.glowxq.core.common.entity.base.BaseVO;
import lombok.Data;

/**
 * 单个测试用例信息封装类
 */
@Data
public class TestCaseInfo implements Comparable<TestCaseInfo>, BaseVO {

    /**
     * 输入文件名
     */
    private String input;

    /**
     * 输出文件名
     */
    private String output;

    /**
     * 子任务分组编号（可选）
     */
    private Integer groupNum;

    // 构造器
    public TestCaseInfo(String input, String output, Integer groupNum) {
        this.input = input;
        this.output = output;
        this.groupNum = groupNum;
    }

    /**
     * 自然排序规则：按文件名长度和字母顺序排序
     */
    @Override
    public int compareTo(TestCaseInfo o) {
        String a = this.input.split("\\.")[0];
        String b = o.input.split("\\.")[0];
        if (a.length() != b.length()) {
            return Integer.compare(a.length(), b.length());
        }
        return a.compareTo(b);
    }
}
