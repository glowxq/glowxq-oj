package com.glowxq.oj.problem.pojo.po;

import com.glowxq.core.common.entity.base.BaseEntity;
import com.glowxq.mysql.EntityChangeListener;
import com.glowxq.oj.judge.enums.JudgeMode;
import com.glowxq.oj.problem.enums.ProblemSourceType;
import com.glowxq.oj.problem.enums.ProblemType;
import com.glowxq.oj.problem.enums.ProgramType;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.time.LocalDateTime;

/**
 * <p>
 * 题目
 * </p>
 *
 * @author glowxq
 * @since 2025-03-17
 */
@Data
@Table(value = "problem", onInsert = EntityChangeListener.class, onUpdate = EntityChangeListener.class)
@Schema(description = "题目")
public class Problem implements BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    @Schema(description = "ID")
    private Long id;

    @Schema(description = "题目的自定义ID 例如(HOJ-1000)")
    private String problemKey;

    @Schema(description = "题目")
    private String title;

    @Schema(description = "作者")
    private String author;

    @Schema(description = "0为ACM,1为OI")
    private Integer programType;

    @Schema(description = "题目类型")
    private String problemType;

    @Schema(description = "来源类型")
    private String sourceType;

    @Schema(description = "视频链接")
    private String linkVideo;

    @Schema(description = "单位ms")
    private Integer timeLimit;

    @Schema(description = "单位kb")
    private Integer memoryLimit;

    @Schema(description = "单位mb")
    private Integer stackLimit;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "输入描述")
    private String input;

    @Schema(description = "输出描述")
    private String output;

    @Schema(description = "题面样例")
    private String examples;

    @Schema(description = "题目难度,0简单|1中等|2困难")
    private Integer difficulty;

    @Schema(description = "备注,提醒")
    private String hint;

    @Schema(description = "默认为1公开|2为私有|3为比赛题目")
    private Integer auth;

    @Schema(description = "当该题目为io题目时的分数")
    private Integer ioScore;

    @Schema(description = "非编程题目总分")
    private Integer score;

    @Schema(description = "题目来源")
    private String source;

    @Schema(description = "题目评测模式,default、spj、interactive")
    private String judgeMode;

    @Schema(description = "题目样例评测模式,default,subtask_lowest,subtask_average")
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
    private Integer applyPublicProgress;

    @Schema(description = "题目指定的file io输入文件的名称")
    private String ioReadFileName;

    @Schema(description = "题目指定的file io输出文件的名称")
    private String ioWriteFileName;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "租户ID")
    private String tenantId;

    @Schema(description = "是否删除")
    private String delFlag;

    @Schema(description = "创建人ID")
    private Long createId;

    @Schema(description = "更新人ID")
    private Long updateId;

    public JudgeMode judgeMode() {
        return JudgeMode.getJudgeMode(judgeMode);
    }

    public ProgramType programType() {
        return ProgramType.matchCode(programType);
    }

    public ProblemType problemType() {
        return ProblemType.matchCode(problemType);
    }

    public ProblemSourceType sourceType() {
        return ProblemSourceType.matchCode(sourceType);
    }

    public Boolean fixedProblem() {
        return problemType().isFixed();
    }
}