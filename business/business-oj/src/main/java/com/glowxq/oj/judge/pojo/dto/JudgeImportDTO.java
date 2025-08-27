package com.glowxq.oj.judge.pojo.dto;

import cn.idev.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
/**
 * <p>
 * Judge导入DTO
 * </p>
 *
 * @author glowxq
 * @since 2025-03-17
 */
@Data
@Schema(description = "Judge导入DTO")
public class JudgeImportDTO {

    @ExcelProperty(value = "自定义提交ID")
    @Schema(description =  "自定义提交ID")
    private String judgeKey;

    @ExcelProperty(value = "题目ID")
    @Schema(description =  "题目ID")
    private Long problemId;

    @ExcelProperty(value = "题目KEY")
    @Schema(description =  "题目KEY")
    private String problemKey;

    @ExcelProperty(value = "测评用户id")
    @Schema(description =  "测评用户id")
    private Long userId;

    @ExcelProperty(value = "团队id|不为团队内提交则为0")
    @Schema(description =  "团队id|不为团队内提交则为0")
    private Long groupId;

    @ExcelProperty(value = "比赛id|非比赛题目默认为0")
    @Schema(description =  "比赛id|非比赛题目默认为0")
    private Long contestId;

    @ExcelProperty(value = "比赛中题目排序id|非比赛题目默认为0")
    @Schema(description =  "比赛中题目排序id|非比赛题目默认为0")
    private String contestProblemId;

    @ExcelProperty(value = "测评人姓名")
    @Schema(description = "测评人姓名")
    private String name;

    @ExcelProperty(value = "测评场景类型")
    @Schema(description =  "测评场景类型")
    private String sceneType;

    @ExcelProperty(value = "题目类型")
    @Schema(description =  "题目类型")
    private String problemType;

    @Schema(description =  "提交测评的时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime submitTime;

    @Schema(description =  "开始测评的时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    @Schema(description =  "结束测评的时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    @ExcelProperty(value = "结果码具体参考文档")
    @Schema(description =  "结果码具体参考文档")
    private Integer status;

    @ExcelProperty(value = "错误提醒(编译错误|或者vj提醒)")
    @Schema(description =  "错误提醒(编译错误|或者vj提醒)")
    private String errorMessage;

    @ExcelProperty(value = "运行时间(ms)")
    @Schema(description =  "运行时间(ms)")
    private Integer time;

    @ExcelProperty(value = "运行内存(kb)")
    @Schema(description =  "运行内存(kb)")
    private Integer memory;

    @ExcelProperty(value = "IO判题则不为空")
    @Schema(description =  "IO判题则不为空")
    private Integer score;

    @ExcelProperty(value = "代码长度")
    @Schema(description =  "代码长度")
    private Integer length;

    @ExcelProperty(value = "流程图URL")
    @Schema(description =  "流程图URL")
    private String flowImage;

    @ExcelProperty(value = "代码")
    @Schema(description =  "代码")
    private String code;

    @ExcelProperty(value = "非编程题作答内容")
    @Schema(description =  "非编程题作答内容")
    private String replyOptions;

    @ExcelProperty(value = "代码语言")
    @Schema(description =  "代码语言")
    private String language;

    @ExcelProperty(value = "判题机")
    @Schema(description =  "判题机")
    private String judgeServer;

    @ExcelProperty(value = "提交者所在ip")
    @Schema(description =  "提交者所在ip")
    private String submitIp;

    @ExcelProperty(value = "乐观锁")
    @Schema(description =  "乐观锁")
    private Integer version;

    @ExcelProperty(value = "oi排行榜得分")
    @Schema(description =  "oi排行榜得分")
    private Integer oiRankScore;

    @ExcelProperty(value = "0为仅自己可见|1为全部人可见。")
    @Schema(description =  "0为仅自己可见|1为全部人可见。")
    private Boolean shareEnable;

    @ExcelProperty(value = "是否为人工评测")
    @Schema(description =  "是否为人工评测")
    private Boolean manualEvaluation;

    @ExcelProperty(value = "vjudge判题在其它oj的提交id")
    @Schema(description =  "vjudge判题在其它oj的提交id")
    private String otherJudgeSubmitId;

    @ExcelProperty(value = "vjudge判题在其它oj的提交用户名")
    @Schema(description =  "vjudge判题在其它oj的提交用户名")
    private String otherJudgeUsername;

    @ExcelProperty(value = "vjudge判题在其它oj的提交账号密码")
    @Schema(description =  "vjudge判题在其它oj的提交账号密码")
    private String otherJudgePassword;

}