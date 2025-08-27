package com.glowxq.oj.topic.pojo.po;

import com.glowxq.core.common.entity.base.BaseEntity;
import com.glowxq.mysql.EntityChangeListener;
import com.glowxq.oj.judge.enums.JudgeStatus;
import com.glowxq.oj.problem.enums.ProblemType;
import com.glowxq.oj.topic.enums.TimeRangeType;
import com.glowxq.oj.topic.enums.TopicJudgeType;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * <p>
 * 主题测评记录
 * </p>
 *
 * @author glowxq
 * @since 2025-04-12
 */
@Data
@Table(value = "topic_submit", onInsert = EntityChangeListener.class, onUpdate = EntityChangeListener.class)
@Schema(description = "主题测评记录")
public class TopicSubmit implements BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    @Schema(description = "")
    private Long id;

    @Schema(description = "主题ID")
    private Long topicId;

    @Schema(description = "题目ID")
    private Long problemId;

    @Schema(description = "题目Key")
    private String problemKey;

    @Schema(description = "题目标题ID")
    private String problemTitle;

    @Schema(description = "题目类型")
    private String problemType;

    @Schema(description = "主题测评类型 ACM/OI")
    private Integer topicJudgeType;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "姓名")
    private String name;

    @Schema(description = "昵称")
    private String nickName;

    @Schema(description = "测评状态")
    private Integer judgeStatus;

    @Schema(description = "主题分数")
    private Integer score;

    @Schema(description = "做题用时(分)")
    private Integer useTime;

    @Schema(description = "罚时(分)")
    private Integer punishmentTime;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "租户ID")
    private String tenantId;

    @Schema(description = "创建人ID")
    private Long createId;

    @Schema(description = "更新人ID")
    private Long updateId;

    public JudgeStatus judgeStatus() {
        return JudgeStatus.matchCode(judgeStatus);
    }

    public TopicJudgeType topicJudgeType() {
        return TopicJudgeType.matchCode(topicJudgeType);
    }

    public ProblemType problemType() {
        return ProblemType.matchCode(problemType);
    }

    public void calculateScore(Integer timeLimit, TimeRangeType timeRangeType, LocalDateTime startTime,BigDecimal deductionRate, Integer score) {
        long useTime = Math.abs(ChronoUnit.MINUTES.between(LocalDateTime.now(), startTime));
        if (useTime <= timeLimit) {
            return;
        }
        if (TimeRangeType.PENALTY_GRADUAL.equals(timeRangeType)) {
            // 分数 = 分数 - (分数 * 扣除比例 * 超时小时数量)
            this.score -= (int) (score * deductionRate.doubleValue() * ((useTime - timeLimit) / 60));
        }
        if (TimeRangeType.PENALTY_FIXED.equals(timeRangeType)) {
            // 分数 = 分数 - (分数 * 扣除比例)
            this.score -= (int) (score * deductionRate.doubleValue());
        }
    }
}