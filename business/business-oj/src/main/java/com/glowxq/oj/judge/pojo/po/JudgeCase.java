package com.glowxq.oj.judge.pojo.po;

import com.glowxq.core.common.entity.base.BaseEntity;
import com.glowxq.mysql.EntityChangeListener;
import com.glowxq.oj.judge.enums.JudgeCaseMode;
import com.glowxq.oj.judge.enums.JudgeStatus;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.time.LocalDateTime;

/**
 * <p>
 * 测评用例情况
 * </p>
 *
 * @author glowxq
 * @since 2025-03-17
 */
@Data
@Table(value = "judge_case", onInsert = EntityChangeListener.class, onUpdate = EntityChangeListener.class)
@Schema(description = "测评用例情况")
@Accessors(chain = true)
public class JudgeCase implements BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    @Schema(description = "ID")
    private Long id;

    @Schema(description = "提交判题id")
    private Long judgeId;

    @Schema(description = "自定义测评KEY")
    private String judgeKey;

    @Schema(description = "用户id")
    private Long userId;

    @Schema(description = "题目ID")
    private Long problemId;

    @Schema(description = "题目KEY")
    private String problemKey;

    @Schema(description = "测试样例id")
    private Long caseId;

    @Schema(description = "具体看结果码")
    private Integer status;

    @Schema(description = "测试该样例所用时间ms")
    private Integer time;

    @Schema(description = "测试该样例所用空间KB")
    private Integer memory;

    @Schema(description = "IO得分")
    private Integer score;

    @Schema(description = "subtask分组的组号")
    private Integer groupNum;

    @Schema(description = "排序")
    private Integer seq;

    @Schema(description = "default,subtask_lowest,subtask_average")
    private String mode;

    @Schema(description = "样例输入|比赛不可看")
    private String inputData;

    @Schema(description = "样例输出|比赛不可看")
    private String outputData;

    @Schema(description = "用户样例输出|比赛不可看")
    private String userOutput;

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

    public JudgeCaseMode judgeCaseMode() {
        return JudgeCaseMode.getJudgeMode(mode);
    }

    public JudgeStatus judgeStatus() {
        return JudgeStatus.matchCode(status);
    }
}