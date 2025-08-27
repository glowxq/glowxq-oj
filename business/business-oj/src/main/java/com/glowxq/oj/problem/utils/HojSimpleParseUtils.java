package com.glowxq.oj.problem.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HojSimpleParseUtils {

    private static final String testData = "<input>4 2\n1 3\n2 4</input><output>2</output><input>3 0</input><output>6</output><input>3 2\n1 2\n2 " +
            "1</input><output>0</output>";

    /**
     * 解析包含input/output标签的测试用例数据，转换为markdown格式
     *
     * @param testCaseData 包含input/output标签的测试用例字符串
     * @return markdown格式的测试用例展示
     */
    public static String parseToMarkdown(String testCaseData) {
        if (testCaseData == null || testCaseData.trim().isEmpty()) {
            return "";
        }

        // 正则表达式匹配input/output标签对
        Pattern pattern = Pattern.compile("<input>(.*?)</input><output>(.*?)</output>", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(testCaseData);

        List<TestCase> testCases = new ArrayList<>();

        while (matcher.find()) {
            String input = matcher.group(1).trim();
            String output = matcher.group(2).trim();
            testCases.add(new TestCase(input, output));
        }

        if (testCases.isEmpty()) {
            return "未找到有效的测试用例";
        }
        
        return formatToMarkdown(testCases);
    }

    /**
     * 将测试用例列表格式化为markdown
     */
    private static String formatToMarkdown(List<TestCase> testCases) {
        StringBuilder markdown = new StringBuilder();

        for (int i = 0; i < testCases.size(); i++) {
            TestCase testCase = testCases.get(i);

            markdown.append("**用例 ").append(i + 1).append("**\n\n");
            
            // 输入部分
            markdown.append("**输入：**\n");
            markdown.append("```\n");
            markdown.append(testCase.getInput()).append("\n");
            markdown.append("```\n\n");
            
            // 输出部分
            markdown.append("**输出：**\n");
            markdown.append("```\n");
            markdown.append(testCase.getOutput()).append("\n");
            markdown.append("```\n");

            // 如果不是最后一个用例，添加分隔线
            if (i < testCases.size() - 1) {
                markdown.append("\n---\n\n");
            }
        }

        return markdown.toString();
    }

    /**
     * 替代的表格格式展示方法（更紧凑）
     */
    public static String parseToMarkdownTable(String testCaseData) {
        if (testCaseData == null || testCaseData.trim().isEmpty()) {
            return "";
        }

        Pattern pattern = Pattern.compile("<input>(.*?)</input><output>(.*?)</output>", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(testCaseData);

        List<TestCase> testCases = new ArrayList<>();

        while (matcher.find()) {
            String input = matcher.group(1).trim();
            String output = matcher.group(2).trim();
            testCases.add(new TestCase(input, output));
        }

        if (testCases.isEmpty()) {
            return "未找到有效的测试用例";
        }

        StringBuilder markdown = new StringBuilder();
        markdown.append("| 用例 | 输入 | 输出 |\n");
        markdown.append("|------|------|------|\n");

        for (int i = 0; i < testCases.size(); i++) {
            TestCase testCase = testCases.get(i);
            String input = testCase.getInput().replace("\n", "<br>");
            String output = testCase.getOutput().replace("\n", "<br>");

            markdown.append("| ").append(i + 1).append(" | ");
            markdown.append("`").append(input).append("` | ");
            markdown.append("`").append(output).append("` |\n");
        }

        return markdown.toString();
    }

    /**
     * 测试用例内部类
     */
    private static class TestCase {
        private final String input;
        private final String output;

        public TestCase(String input, String output) {
            this.input = input;
            this.output = output;
        }

        public String getInput() {
            return input;
        }

        public String getOutput() {
            return output;
        }
    }

    /**
     * 测试方法
     */
    public static void main(String[] args) {
        System.out.println("=== 标准格式 ===");
        System.out.println(parseToMarkdown(testData));

        System.out.println("\n=== 表格格式 ===");
        System.out.println(parseToMarkdownTable(testData));
    }
}
