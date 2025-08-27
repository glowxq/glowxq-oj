package com.glowxq.oj.problem.pojo.dto;

import com.glowxq.core.common.entity.base.BaseDTO;
import com.glowxq.core.common.valid.annotation.InEnum;
import com.glowxq.core.util.BeanCopyUtils;
import com.glowxq.oj.judge.enums.JudgeCaseMode;
import com.glowxq.oj.judge.enums.JudgeMode;
import com.glowxq.oj.problem.enums.*;
import com.glowxq.oj.problem.pojo.po.Problem;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>
 * Problem修改DTO
 * </p>
 *
 * @author glowxq
 * @since 2025-03-17
 */
@Data
@NoArgsConstructor
@Schema(description = "Problem修改DTO")
public class ProblemBO implements BaseDTO {

    @Schema(description = "题目的ID")
    private Long id;

    @Schema(description = "题目的自定义ID 例如(G1000)")
    private String problemKey;

    @Schema(description = "题目")
    @NotBlank
    private String title;

    @Schema(description = "0为ACM,1为OI")
    @NotNull
    @InEnum(enumClass = ProgramType.class)
    private Integer programType;

    @Schema(description = "题目类型")
    @NotBlank
    @InEnum(enumClass = ProblemType.class)
    private String problemType;

    @Schema(description = "来源类型")
    @InEnum(enumClass = ProblemSourceType.class)
    private String sourceType;

    /**
     * 视频链接
     */
    @Schema(description = "视频链接")
    private String linkVideo;

    @Schema(description = "单位ms")
    @NotNull
    @Min(value = 0)
    private Integer timeLimit;

    @Schema(description = "单位kb")
    @NotNull
    @Min(value = 0)
    private Integer memoryLimit;

    @Schema(description = "单位mb")
    @NotNull
    @Min(value = 0)
    private Integer stackLimit;

    @Schema(description = "题目描述")
    private String description;

    @Schema(description = "输入描述")
    private String input;

    @Schema(description = "输出描述")
    private String output;

    @Schema(description = "题面样例")
    private String examples;

    @Schema(description = "题目难度 ProblemDifficultyType")
    @InEnum(enumClass = ProblemDifficultyType.class)
    private Integer difficulty;

    @Schema(description = "备注,提醒")
    private String hint;

    @Schema(description = "默认为 ProblemAuthType")
    @InEnum(enumClass = ProblemAuthType.class)
    private Integer auth;

    @Schema(description = "当该题目为io题目时的分数")
    private Integer ioScore;

    @Schema(description = "非编程题目总分")
    private Integer score;

    @Schema(description = "题目来源")
    private String source;

    @Schema(description = "题目评测模式,default、spj、interactive")
    @InEnum(enumClass = JudgeMode.class)
    private String judgeMode;

    @Schema(description = "题目样例评测模式,default,subtask_lowest,subtask_average")
    @InEnum(enumClass = JudgeCaseMode.class)
    private String judgeCaseMode;

    @Schema(description = "题目评测时用户程序的额外文件 json key:name value:content")
    private String userExtraFile;

    @Schema(description = "题目评测时交互或特殊程序的额外文件 json key:name value:content")
    private String judgeExtraFile;

    @Schema(description = "特判程序或交互程序代码")
    private String spjCode;

    @Schema(description = "特判程序或交互程序代码的语言")
    private String spjLanguage;

    @Schema(description = "是否为远程vj判题")
    private Boolean remote;

    @Schema(description = "该题目对应的相关提交代码|用户是否可用分享")
    private Boolean codeShare;

    @Schema(description = "是否默认去除用户代码的文末空格")
    private Boolean removeEndBlank;

    @Schema(description = "是否默认开启该题目的测试样例结果查看")
    private Boolean openCaseResult;

    @Schema(description = "题目测试数据是否是上传文件的")
    private Boolean uploadCase;

    @Schema(description = "在队伍内")
    private Boolean groupProblem;

    @Schema(description = "是否是file io自定义输入输出文件模式")
    private Boolean fileIo;

    @Schema(description = "必须上传图片")
    private Boolean requireImage;

    @Schema(description = "题目测试数据的版本号")
    private String caseVersion;

    @Schema(description = "修改题目的管理员用户名")
    private String modifiedUser;

    @Schema(description = "申请公开的进度：null为未申请|1为申请中|2为申请通过|3为申请拒绝")
    @InEnum(enumClass = ProblemApplyPublicProgress.class)
    private Integer applyPublicProgress;

    @Schema(description = "题目指定的file io输入文件的名称")
    private String ioReadFileName;

    @Schema(description = "题目指定的file io输出文件的名称")
    private String ioWriteFileName;

    public ProblemBO(Problem problem) {
        BeanCopyUtils.copy(problem, this);
    }

    @Override
    public Problem buildEntity() {
        return BeanCopyUtils.copy(this, Problem.class);
    }
}