package com.glowxq.oj.problem.pojo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * 从HOJ压缩包导入编程题的DTO
 */
@Data
@Schema(description = "从HOJ系统导入的编程问题DTO")
public class ProblemImportFromHojDTO {

    /**
     * 题目详细信息
     */
    @Schema(description = "题目详细信息")
    private Problem problem;

    @Schema(description = "上传测试用例的临时文件夹")
    private String uploadTestCaseDir;

    /**
     * 支持的语言列表
     */
    @Schema(description = "支持的语言列表")
    private List<String> languages;

    /**
     * 示例测试用例列表
     */
    @Schema(description = "示例测试用例列表")
    private List<Sample> samples;

    /**
     * 题目标签列表
     */
    @Schema(description = "题目标签列表")
    private List<String> tags;

    /**
     * 代码模板列表
     */
    @Schema(description = "代码模板列表")
    private List<CodeTemplate> codeTemplates;

    /**
     * 判题模式
     */
    @Schema(description = "判题模式")
    private String judgeMode;

    /**
     * 测试用例信息
     */
    @Schema(description = "测试用例信息")
    private TestCaseInfo testCaseInfo;

    /**
     * 输入文件映射 (文件名 -> 文件内容)
     */
    @Schema(description = "输入文件映射 (文件名 -> 文件内容)")
    private Map<String, File> inputFiles;

    /**
     * 输出文件映射 (文件名 -> 文件内容)
     */
    @Schema(description = "输出文件映射 (文件名 -> 文件内容)")
    private Map<String, File> outputFiles;

    /**
     * 题目详细信息内部类
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "题目详细信息")
    public static class Problem {

        /**
         * 栈限制大小
         */
        @Schema(description = "栈限制大小")
        private Integer stackLimit;

        /**
         * 最后修改用户
         */
        @Schema(description = "最后修改用户")
        private String modifiedUser;

        /**
         * 判题用例模式
         */
        @Schema(description = "判题用例模式")
        private String judgeCaseMode;

        @Schema(description = "题目评测时用户程序的额外文件 json key:name value:content")
        private String userExtraFile;

        @Schema(description = "题目评测时交互或特殊程序的额外文件 json key:name value:content")
        private String judgeExtraFile;

        @Schema(description = "特判程序或交互程序代码")
        private String spjCode;

        @Schema(description = "特判程序或交互程序代码的语言")
        private String spjLanguage;

        /**
         * 权限级别
         */
        @Schema(description = "权限级别")
        private Integer auth;

        /**
         * 题目描述（Markdown格式）
         */
        @Schema(description = "题目描述（Markdown格式）")
        private String description;

        /**
         * 题目来源
         */
        @Schema(description = "题目来源")
        private String source;

        /**
         * 题目标题
         */
        @Schema(description = "题目标题")
        private String title;

        /**
         * 视频链接
         */
        @Schema(description = "视频链接")
        private String linkVideo;

        /**
         * 题目类型
         */
        @Schema(description = "题目类型")
        private Integer type;

        /**
         * 是否需要图片
         */
        @Schema(description = "是否需要图片")
        private Boolean requireImage;

        @Schema(description = "题目测试数据的版本号")
        private String caseVersion;

        /**
         * 输出要求说明
         */
        @Schema(description = "输出要求说明")
        private String output;

        /**
         * IO分数占比
         */
        @Schema(description = "IO分数占比")
        private Integer ioScore;

        /**
         * 是否共享代码
         */
        @Schema(description = "是否共享代码")
        private Boolean codeShare;

        /**
         * 是否为文件IO
         */
        @Schema(description = "是否为文件IO")
        private Boolean isFileIO;

        /**
         * 是否为远程判题
         */
        @Schema(description = "是否为远程判题")
        private Boolean isRemote;

        /**
         * 时间限制（毫秒）
         */
        @Schema(description = "时间限制（毫秒）")
        private Integer timeLimit;

        /**
         * 题目难度
         */
        @Schema(description = "题目难度")
        private Integer difficulty;

        /**
         * 输入要求说明
         */
        @Schema(description = "输入要求说明")
        private String input;

        /**
         * 示例输入输出
         */
        @Schema(description = "示例输入输出")
        private String examples;

        /**
         * 来源类型
         */
        @Schema(description = "来源类型")
        private String sourceType;

        /**
         * 题目提示
         */
        @Schema(description = "题目提示")
        private String hint;

        /**
         * 是否移除末尾空白
         */
        @Schema(description = "是否移除末尾空白")
        private Boolean isRemoveEndBlank;

        /**
         * 是否开放用例结果
         */
        @Schema(description = "是否开放用例结果")
        private Boolean openCaseResult;

        /**
         * 内存限制（MB）
         */
        @Schema(description = "内存限制（MB）")
        private Integer memoryLimit;

        /**
         * 题目ID
         */
        @Schema(description = "题目ID")
        private String problemId;

        /**
         * 是否为组题
         */
        @Schema(description = "是否为组题")
        private Boolean isGroup;

        /**
         * 问题类型
         */
        @Schema(description = "问题类型")
        private String problemType;

        /**
         * 是否上传测试用例
         */
        @Schema(description = "是否上传测试用例")
        private Boolean isUploadCase;

        /**
         * 判题模式
         */
        @Schema(description = "判题模式")
        private String judgeMode;

        @Schema(description = "显示部分数据")
        private String displayPartData;

        @Schema(description = "题目指定的file io输入文件的名称")
        private String ioReadFileName;

        @Schema(description = "题目指定的file io输出文件的名称")
        private String ioWriteFileName;
    }

    /**
     * 示例测试用例内部类
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "示例测试用例")
    public static class Sample {

        /**
         * 输出文件名
         */
        @Schema(description = "输出文件名")
        private String output;

        /**
         * 输入文件名
         */
        @Schema(description = "输入文件名")
        private String input;

        /**
         * 该测试用例分数
         */
        @Schema(description = "该测试用例分数")
        private Integer score;

        /**
         * 测试用例组编号
         */
        @Schema(description = "测试用例组编号")
        private Integer groupNum;
    }

    /**
     * 代码模板内部类
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "代码模板")
    public static class CodeTemplate {

        /**
         * 代码内容
         */
        @Schema(description = "代码内容")
        private String code;

        /**
         * 编程语言
         */
        @Schema(description = "编程语言")
        private String language;
    }

    /**
     * 测试用例信息内部类
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "测试用例信息")
    public static class TestCaseInfo {

        /**
         * 测试用例模式
         */
        @Schema(description = "测试用例模式")
        private String mode;

        /**
         * 判题用例模式
         */
        @Schema(description = "判题用例模式")
        private String judgeCaseMode;

        /**
         * 版本号
         */
        @Schema(description = "版本号")
        private String version;

        /**
         * 测试用例数量
         */
        @Schema(description = "测试用例数量")
        private Integer testCasesSize;

        /**
         * 测试用例列表
         */
        @Schema(description = "测试用例列表")
        private List<TestCase> testCases;
    }

    /**
     * 测试用例内部类
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "测试用例")
    public static class TestCase {

        /**
         * 用例ID
         */
        @Schema(description = "用例ID")
        private Long caseId;

        /**
         * 分数
         */
        @Schema(description = "分数")
        private Integer score;

        /**
         * 输入文件名
         */
        @Schema(description = "输入文件名")
        private String inputName;

        /**
         * 输出文件名
         */
        @Schema(description = "输出文件名")
        private String outputName;

        /**
         * 输出文件MD5
         */
        @Schema(description = "输出文件MD5")
        private String outputMd5;

        /**
         * 输出文件大小
         */
        @Schema(description = "输出文件大小")
        private Integer outputSize;

        /**
         * 去除所有空白后的输出文件MD5
         */
        @Schema(description = "去除所有空白后的输出文件MD5")
        private String allStrippedOutputMd5;

        /**
         * 去除EOF空白后的输出文件MD5
         */
        @Schema(description = "去除EOF空白后的输出文件MD5")
        @JsonProperty("EOFStrippedOutputMd5")
        private String EOFStrippedOutputMd5;

        /**
         * 测试用例组编号
         */
        private Integer groupNum;
    }
}