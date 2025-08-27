package com.glowxq.oj.problem.pojo.vo;

import cn.idev.excel.annotation.ExcelIgnore;
import cn.idev.excel.annotation.ExcelProperty;
import com.glowxq.core.common.entity.base.BaseVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * <p>
 * Problem返回vo
 * </p>
 *
 * @author glowxq
 * @since 2025-03-17
 */
@Data
@Schema(description = "Problem返回vo")
public class ProblemVO implements BaseVO {

    @ExcelIgnore
    @Schema(description = "ID")
    private String id;

    @ExcelProperty(value = "题目的自定义ID 例如(HOJ-1000)")
    @Schema(description = "题目的自定义ID 例如(HOJ-1000)")
    private String problemKey;

    @ExcelProperty(value = "题目")
    @Schema(description = "题目")
    private String title;

    @ExcelProperty(value = "作者")
    @Schema(description = "作者")
    private String author;

    @ExcelProperty(value = "0为ACM,1为OI")
    @Schema(description = "0为ACM,1为OI")
    private Integer programType;

    @ExcelProperty(value = "题目类型")
    @Schema(description = "题目类型")
    private String problemType;

    @ExcelProperty(value = "来源类型")
    @Schema(description = "来源类型")
    private String sourceType;

    @ExcelProperty(value = "单位ms")
    @Schema(description = "单位ms")
    private Integer timeLimit;

    @ExcelProperty(value = "单位kb")
    @Schema(description = "单位kb")
    private Integer memoryLimit;

    @ExcelProperty(value = "单位mb")
    @Schema(description = "单位mb")
    private Integer stackLimit;

    @ExcelProperty(value = "描述")
    @Schema(description = "描述")
    private String description;

    @ExcelProperty(value = "输入描述")
    @Schema(description = "输入描述")
    private String input;

    @ExcelProperty(value = "输出描述")
    @Schema(description = "输出描述")
    private String output;

    @ExcelProperty(value = "题面样例")
    @Schema(description = "题面样例")
    private String examples;

    @ExcelProperty(value = "题目难度,0简单|1中等|2困难")
    @Schema(description = "题目难度,0简单|1中等|2困难")
    private Integer difficulty;

    @ExcelProperty(value = "备注,提醒")
    @Schema(description = "备注,提醒")
    private String hint;

    @ExcelProperty(value = "默认为1公开|2为私有|3为比赛题目")
    @Schema(description = "默认为1公开|2为私有|3为比赛题目")
    private Integer auth;

    @ExcelProperty(value = "当该题目为io题目时的分数")
    @Schema(description = "当该题目为io题目时的分数")
    private Integer ioScore;

    @ExcelProperty(value = "非编程题目总分")
    @Schema(description = "非编程题目总分")
    private Integer score;

    @ExcelProperty(value = "题目来源")
    @Schema(description = "题目来源")
    private String source;

    @ExcelProperty(value = "题目评测模式,default、spj、interactive")
    @Schema(description = "题目评测模式,default、spj、interactive")
    private String judgeMode;

    @ExcelProperty(value = "题目样例评测模式,default,subtask_lowest,subtask_average")
    @Schema(description = "题目样例评测模式,default,subtask_lowest,subtask_average")
    private String judgeCaseMode;

    @ExcelProperty(value = "题目评测时用户程序的额外文件 json key:name value:content")
    @Schema(description = "题目评测时用户程序的额外文件 json key:name value:content")
    private String userExtraFile;

    @ExcelProperty(value = "题目评测时交互或特殊程序的额外文件 json key:name value:content")
    @Schema(description = "题目评测时交互或特殊程序的额外文件 json key:name value:content")
    private String judgeExtraFile;

    @ExcelProperty(value = "特判程序或交互程序代码")
    @Schema(description = "特判程序或交互程序代码")
    private String spjCode;

    @ExcelProperty(value = "特判程序或交互程序代码的语言")
    @Schema(description = "特判程序或交互程序代码的语言")
    private String spjLanguage;

    @ExcelProperty(value = "是否为远程vj判题")
    @Schema(description = "是否为远程vj判题")
    private Boolean remote;

    @ExcelProperty(value = "该题目对应的相关提交代码|用户是否可用分享")
    @Schema(description = "该题目对应的相关提交代码|用户是否可用分享")
    private Boolean codeShare;

    @ExcelProperty(value = "是否默认去除用户代码的文末空格")
    @Schema(description = "是否默认去除用户代码的文末空格")
    private Boolean removeEndBlank;

    @ExcelProperty(value = "是否默认开启该题目的测试样例结果查看")
    @Schema(description = "是否默认开启该题目的测试样例结果查看")
    private Boolean openCaseResult;

    @ExcelProperty(value = "题目测试数据是否是上传文件的")
    @Schema(description = "题目测试数据是否是上传文件的")
    private Boolean uploadCase;

    @ExcelProperty(value = "在队伍内")
    @Schema(description = "在队伍内")
    private Boolean groupProblem;

    @ExcelProperty(value = "是否是file io自定义输入输出文件模式")
    @Schema(description = "是否是file io自定义输入输出文件模式")
    private Boolean fileIo;

    @ExcelProperty(value = "必须上传图片")
    @Schema(description = "必须上传图片")
    private Boolean requireImage;

    @ExcelProperty(value = "题目测试数据的版本号")
    @Schema(description = "题目测试数据的版本号")
    private String caseVersion;

    @ExcelProperty(value = "修改题目的管理员用户名")
    @Schema(description = "修改题目的管理员用户名")
    private String modifiedUser;

    @ExcelProperty(value = "申请公开的进度：null为未申请|1为申请中|2为申请通过|3为申请拒绝")
    @Schema(description = "申请公开的进度：null为未申请|1为申请中|2为申请通过|3为申请拒绝")
    private Integer applyPublicProgress;

    @ExcelProperty(value = "题目指定的file io输入文件的名称")
    @Schema(description = "题目指定的file io输入文件的名称")
    private String ioReadFileName;

    @ExcelProperty(value = "题目指定的file io输出文件的名称")
    @Schema(description = "题目指定的file io输出文件的名称")
    private String ioWriteFileName;
}