package com.glowxq.oj.problem.pojo.dto;

import com.glowxq.oj.judge.enums.JudgeMode;
import com.glowxq.oj.problem.enums.ProblemCaseType;
import com.glowxq.oj.problem.enums.ProblemType;
import com.glowxq.oj.problem.pojo.po.ProblemCase;
import com.glowxq.system.meta.pojo.po.MetaTag;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

/**
 * <p>
 * Problem添加DTO
 * </p>
 *
 * @author glowxq
 * @since 2025-03-17
 */
@Data
@Schema(description = "Problem添加DTO")
public class ProblemCreateUpdateDTO {

    /**
     * 题目基本信息对象，包含题目内容、难度等核心属性
     */
    @NotNull(message = "题目基本信息对象不能为空")
    @Valid
    private ProblemBO problemBo;

    /**
     * 测试用例文件上传目录路径
     * <p>当isUploadTestCase为true时有效，指定测试用例文件的存储位置</p>
     */
    private String uploadTestcaseDir;

    /**
     * 是否自动删除测试用例文件上传目录，默认创建题目后删除，导入题目时不删除
     */
    private Boolean autoDeleteUploadTestcaseDir = false;

    /**
     * 选项列表（适用于选择题型），存储题目所有可选答案项
     */

    private List<ProblemOptionBO> options;

    /**
     * 样例测试用例集合，用于展示题目输入输出示例（前端可见部分）
     */
    private List<ProblemCaseBO> problemCaseDataList;

    /**
     * 是否允许修改代码模式配置
     * <p>控制是否开放代码编辑器模式切换功能（如严格模式/自由编码模式）</p>
     */
    private Boolean changeModeCode;

    /**
     * 是否允许修改判题用例模式
     * <p>控制是否开放判题用例的编辑模式（如普通用例/特殊评判用例）</p>
     */
    private Boolean changeJudgeCaseMode;

    /**
     * 允许使用的编程语言列表
     * <p>定义题目支持的编程语言集合（如Java、Python等）</p>
     */
    private List<Long> languageIds;

    /**
     * 题目标签集合
     * <p>用于题目分类的关键词标签（如算法、数据结构等分类标识）</p>
     */
    private List<Long> tagIds;

    /**
     * 题目标签集合
     */
    private List<MetaTag> tagList;

    /**
     * 代码模板集合
     * <p>存储不同编程语言的初始代码模板（为不同语言提供预设代码结构）</p>
     */
    private List<ProblemCodeTemplateCreateDTO> codeTemplates;

    public ProblemType problemType() {
        return problemBo.buildEntity().problemType();
    }

    public JudgeMode judgeMode() {
        return problemBo.buildEntity().judgeMode();
    }

    public ProblemCaseType problemCaseType() {
        if (CollectionUtils.isEmpty(problemCaseDataList)) {
            return null;
        }
        ProblemCase problemCase = problemCaseDataList.getFirst().buildEntity();
        return problemCase.problemCaseType();
    }
}