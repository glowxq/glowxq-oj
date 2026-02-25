package com.glowxq.oj.problem.pojo.dto.hydro;

import com.fasterxml.jackson.annotation.JsonAlias;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 从Hydro压缩包导入编程题的DTO
 */
@Data
@Schema(description = "从Hydro系统导入的编程问题DTO")
public class ProblemImportFromHydroDTO {
    /**
     * 题目 Yaml 信息
     */
    @Schema(description = "题目 Yaml 信息")
    private ProblemYamlInfo problemYamlInfo;

    /**
     * 题目 Markdown 信息
     */
    @Schema(description = "题目 Markdown 信息")
    private ProblemMarkdownInfo problemMarkdownInfo;

    /**
     * 配置 Yaml 信息
     */
    @Schema(description = "配置 Yaml 信息")
    private ConfigYamlInfo configYamlInfo;

    /**
     * 输入文件映射 (文件名 -> 文件内容)
     */
    @Schema(description = "输入文件映射 (文件名 -> 文件)")
    private Map<String, File> inputFiles;

    /**
     * 输出文件映射 (文件名 -> 文件内容)
     */
    @Schema(description = "输出文件映射 (文件名 -> 文件)")
    private Map<String, File> outputFiles;

    @Schema(description = "测试数据文件夹")
    private File testDataFolder;

    /**
     * 题目 Yaml 信息内部类
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "题目 Yaml 信息内部类")
    public static class ProblemYamlInfo {
        /**
         * 唯一标识符
         */
        @Schema(description = "唯一标识符")
        private String pid;

        /**
         * 所有者 ID
         */
        @Schema(description = "所有者 ID")
        private Integer owner;

        /**
         * 标题
         */
        @Schema(description = "标题")
        private String title;

        /**
         * 标签列表
         */
        @Schema(description = "标签列表")
        private List<String> tag;

        /**
         * 提交次数
         */
        @Schema(description = "提交次数")
        @JsonAlias({"nSubmit", "nsubmit"})
        private Integer nSubmit;

        /**
         * 通过次数
         */
        @Schema(description = "通过次数")
        @JsonAlias({"nAccept", "naccept"})
        private Integer nAccept;

    }

    /**
     * 题目 Markdown 信息内部类
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "题目 Markdown 信息")
    public static class ProblemMarkdownInfo {
        /**
         * 题目输入输出样例
         */
        @Schema(description = "题目输入输出样例")
        private String examples;

        /**
         * 题目描述
         */
        @Schema(description = "题目描述")
        private String description;
    }

    /**
     * 配置 Yaml 信息内部类
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "配置 Yaml 信息")
    public static class ConfigYamlInfo {
        /**
         * 题目类型，可以为 default(比对输出，可以含spj), objective(客观题), interactive(交互题)
         */
        @Schema(description = "题目类型，可以为 default(比对输出，可以含spj), objective(客观题), interactive(交互题)")
        private String type;

        /**
         * 全局时间限制（此处的限制优先级低于测试点的限制）
         */
        @Schema(description = "全局时间限制（此处的限制优先级低于测试点的限制）")
        private String time;

        /**
         * 全局内存限制（此处的限制优先级低于测试点的限制）
         */
        @Schema(description = "全局内存限制（此处的限制优先级低于测试点的限制）")
        private String memory;

        /**
         * 输入输出文件名（例：使用 foo.in 和 foo.out），若使用标准 IO 删除此配置项即可
         */
        @Schema(description = "输入输出文件名（例：使用 foo.in 和 foo.out），若使用标准 IO 删除此配置项即可")
        private String filename;

        /**
         * 此部分设置当题目类型为 default 时生效
         * 比较器类型，支持的值有 default（直接比对，忽略行末空格和文件末换行）, ccr, cena, hustoj, lemon, qduoj, syzoj, testlib(比较常用)
         */
        @Schema(description = "比较器类型，支持的值有 default（直接比对，忽略行末空格和文件末换行）, ccr, cena, hustoj, lemon, qduoj, syzoj, testlib(比较常用)")
        private String checker_type;

        /**
         * 比较器文件（当比较器类型不为 default 时填写）
         * 文件路径（位于压缩包中的路径）
         * #将通过扩展名识别语言，与编译命令处一致。在默认配置下，C++ 扩展名应为 .cc 而非 .cpp
         */
        @Schema(description = """
                比较器文件（当比较器类型不为 default 时填写）
                文件路径（位于压缩包中的路径）
                将通过扩展名识别语言，与编译命令处一致。在默认配置下，C++ 扩展名应为 .cc 而非 .cpp""")
        private String checker;

        /**
         * 此部分设置当题目类型为interactive时生效
         * 交互器路径（位于压缩包中的路径）
         */
        @Schema(description = "交互器路径（位于压缩包中的路径）")
        private String interactor;

        /**
         * 用户额外文件
         */
        @Schema(description = "用户额外文件")
        private List<String> user_extra_files;

        /**
         * 判题额外文件
         */
        @Schema(description = "判题额外文件")
        private List<String> judge_extra_files;

        /**
         * 测试点列表
         */
        @Schema(description = "测试点列表")
        private List<HashMap<String, String>> cases;

        /**
         * 单个测试点分数
         */
        @Schema(description = "单个测试点分数")
        private Integer score;

        /**
         * 支持的语言列表
         */
        @Schema(description = "支持的语言列表")
        private List<String> langs;

        /**
         * 子任务列表
         */
        @Schema(description = "子任务列表")
        private List<HashMap<String, Object>> subtasks;
    }
}
