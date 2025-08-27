package com.glowxq.oj.judge.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * <p>
 * Judge添加DTO
 * </p>
 *
 * @author glowxq
 * @since 2025-03-17
 */
@Data
@Schema(description = "Judge添加DTO")
public class JudgeCreateDTO {

   @Schema(description =  "自定义提交ID")
   private String judgeKey;

   @Schema(description =  "题目ID")
   private Long problemId;

   @Schema(description =  "题目KEY")
   private String problemKey;

   @Schema(description =  "测评用户id")
   private Long userId;

   @Schema(description =  "团队id|不为团队内提交则为0")
   private Long groupId;

   @Schema(description =  "比赛id|非比赛题目默认为0")
   private Long contestId;

   @Schema(description =  "比赛中题目排序id|非比赛题目默认为0")
   private String contestProblemId;

   @Schema(description = "测评人姓名")
   private String name;

   @Schema(description =  "测评场景类型")
   private String sceneType;

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

   @Schema(description =  "结果码具体参考文档")
   private Integer status;

   @Schema(description =  "错误提醒(编译错误|或者vj提醒)")
   private String errorMessage;

   @Schema(description =  "运行时间(ms)")
   private Integer time;

   @Schema(description =  "运行内存(kb)")
   private Integer memory;

   @Schema(description =  "IO判题则不为空")
   private Integer score;

   @Schema(description =  "代码长度")
   private Integer length;

   @Schema(description =  "流程图URL")
   private String flowImage;

   @Schema(description =  "代码")
   private String code;

   @Schema(description =  "非编程题作答内容")
   private String replyOptions;

   @Schema(description =  "代码语言")
   private String language;

   @Schema(description =  "判题机")
   private String judgeServer;

   @Schema(description =  "提交者所在ip")
   private String submitIp;

   @Schema(description =  "乐观锁")
   private Integer version;

   @Schema(description =  "oi排行榜得分")
   private Integer oiRankScore;

   @Schema(description =  "0为仅自己可见|1为全部人可见。")
   private Boolean shareEnable;

   @Schema(description =  "是否为人工评测")
   private Boolean manualEvaluation;

   @Schema(description =  "vjudge判题在其它oj的提交id")
   private String otherJudgeSubmitId;

   @Schema(description =  "vjudge判题在其它oj的提交用户名")
   private String otherJudgeUsername;

   @Schema(description =  "vjudge判题在其它oj的提交账号密码")
   private String otherJudgePassword;

}