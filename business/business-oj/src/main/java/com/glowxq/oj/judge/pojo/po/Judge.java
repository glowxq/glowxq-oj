package com.glowxq.oj.judge.pojo.po;

import com.glowxq.core.common.entity.base.BaseEntity;
import com.glowxq.core.util.JsonUtils;
import com.glowxq.mysql.EntityChangeListener;
import com.glowxq.oj.judge.enums.JudgeSceneType;
import com.glowxq.oj.judge.enums.JudgeStatus;
import com.glowxq.oj.judge.enums.SubmitType;
import com.glowxq.oj.problem.enums.ProblemType;
import com.glowxq.oj.problem.pojo.po.ProblemOption;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 测评记录
 * </p>
 *
 * @author glowxq
 * @since 2025-03-17
 */
@Data
@Table(value = "judge", onInsert = EntityChangeListener.class, onUpdate = EntityChangeListener.class)
@Schema(description = "测评记录")
public class Judge implements BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    @Schema(description = "ID")
    private Long id;

    @Schema(description = "自定义测评KEY")
    private String judgeKey;

    @Schema(description = "题目ID")
    private Long problemId;

    @Schema(description = "题目KEY")
    private String problemKey;

    @Schema(description = "题目标题")
    private String problemTitle;

    @Schema(description = "测评用户id")
    private Long userId;

    @Schema(description = "团队id|不为团队内提交则为0")
    private Long groupId;

    @Schema(description = "比赛id|非比赛题目默认为0")
    private Long contestId;

    @Schema(description = "提交类型业务ID 比赛、训练、班级等等")
    private Long businessId;

    @Schema(description = "测评人姓名")
    private String name;

    @Schema(description = "提交类型")
    private String submitType;

    @Schema(description = "测评场景类型")
    private String sceneType;

    @Schema(description = "题目类型")
    private String problemType;

    @Schema(description = "总做题用时(分)")
    private Integer useTime;

    @Schema(description = "自动提交")
    private Boolean autoSubmit;

    @Schema(description = "提交测评的时间")
    private LocalDateTime submitTime;

    @Schema(description = "开始测评的时间")
    private LocalDateTime startTime;

    @Schema(description = "结束测评的时间")
    private LocalDateTime endTime;

    @Schema(description = "结果码具体参考文档")
    private Integer status;

    @Schema(description = "错误提醒(编译错误|或者vj提醒)")
    private String errorMessage;

    @Schema(description = "运行时间(ms)")
    private Integer time;

    @Schema(description = "运行内存(kb)")
    private Integer memory;

    @Schema(description = "IO判题则不为空")
    private Integer score;

    @Schema(description = "代码长度")
    private Integer length;

    @Schema(description = "流程图URL")
    private String flowImage;

    @Schema(description = "代码")
    private String code;

    @Schema(description = "非编程题作答内容")
    private String replyOptions;

    @Schema(description = "代码语言")
    private String language;

    @Schema(description = "判题机")
    private String judgeServer;

    @Schema(description = "提交者所在ip")
    private String submitIp;

    @Schema(description = "乐观锁")
    private Integer version;

    @Schema(description = "oi排行榜得分")
    private Integer oiRankScore;

    @Schema(description = "0为仅自己可见|1为全部人可见。")
    private Boolean shareEnable;

    @Schema(description = "是否为人工评测")
    private Boolean manualEvaluation;

    @Schema(description = "vjudge判题在其它oj的提交id")
    private String otherJudgeSubmitId;

    @Schema(description = "vjudge判题在其它oj的提交用户名")
    private String otherJudgeUsername;

    @Schema(description = "vjudge判题在其它oj的提交账号密码")
    private String otherJudgePassword;

    @Schema(description = "异常堆栈信息")
    private String exceptionStackTrace;

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

    public JudgeStatus status() {
        return JudgeStatus.matchCode(status);
    }

    public ProblemType problemType() {
        return ProblemType.matchCode(problemType);
    }

    public List<ProblemOption> replyOptions() {
        return JsonUtils.parseArray(replyOptions, ProblemOption.class);
    }

    public SubmitType submitType() {
        return SubmitType.matchCode(submitType);
    }

    public JudgeSceneType sceneType() {
        return JudgeSceneType.matchCode(sceneType);
    }

    public Boolean isAc() {
        return JudgeStatus.STATUS_ACCEPTED.equals(status());
    }

    public Boolean isNotAc() {
        return JudgeStatus.STATUS_ACCEPTED.equals(status());
    }
}