package com.glowxq.oj.topic.pojo.po;

import cn.dev33.satoken.stp.StpUtil;
import com.glowxq.core.common.entity.base.BaseEntity;
import com.glowxq.core.common.exception.common.BusinessException;
import com.glowxq.mysql.EntityChangeListener;
import com.glowxq.oj.constants.PermissionConstant;
import com.glowxq.oj.topic.enums.OiScoreType;
import com.glowxq.oj.topic.enums.TimeRangeType;
import com.glowxq.oj.topic.enums.TopicJudgeType;
import com.glowxq.oj.topic.enums.TopicType;
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
 * 主题
 * </p>
 *
 * @author glowxq
 * @since 2025-04-12
 */
@Data
@Table(value = "topic", onInsert = EntityChangeListener.class, onUpdate = EntityChangeListener.class)
@Schema(description = "主题")
public class Topic implements BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    @Schema(description = "")
    private Long id;

    @Schema(description = "主题名")
    private String name;

    @Schema(description = "主题代码")
    private String code;

    @Schema(description = "负责人ID")
    private Long principalUserId;

    @Schema(description = "负责人姓名")
    private String principalName;

    @Schema(description = "主题密码(密码不为空,则打开主题时必须输入正确密码)")
    private String password;

    @Schema(description = "主题描述")
    private String description;

    @Schema(description = "时间范围策略")
    private String timeRangeType;

    @Schema(description = "扣分参数")
    private BigDecimal deductionRate;

    @Schema(description = "主题类型")
    private String topicType;

    @Schema(description = "主题测评类型 ACM/OI")
    private Integer topicJudgeType;

    @Schema(description = "提前封榜(时) -1为全程封榜 0为不封榜")
    private Integer sealedTime;

    @Schema(description = "时间限制(分) 超时自动提交 -1为不限时间")
    private Integer timeLimit;

    @Schema(description = "罚时(分)")
    private Integer punishmentTime;

    @Schema(description = "OI得分类型 最近得分/最高得分")
    private String oiScoreType;

    @Schema(description = "主题颜色")
    private String color;

    @Schema(description = "公共主题")
    private Boolean common;

    @Schema(description = "首页展示")
    private Boolean homeShow;

    @Schema(description = "启用")
    private Boolean enable;

    @Schema(description = "开始时间")
    private LocalDateTime startTime;

    @Schema(description = "截止时间")
    private LocalDateTime endTime;

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

    public OiScoreType oiScoreType() {
        return OiScoreType.matchCode(oiScoreType);
    }

    public TimeRangeType timeRangeType() {
        return TimeRangeType.matchCode(timeRangeType);
    }

    public TopicJudgeType topicJudgeType() {
        return TopicJudgeType.matchCode(topicJudgeType);
    }

    public TopicType topicType() {
        return TopicType.matchCode(topicType);
    }

    /**
     * 检查时间范围
     *
     * @param startTime
     */
    public void checkTimeRange(LocalDateTime startTime) {
        // 时间限制小于0 则为无限时间
        Integer timeLimit = this.getTimeLimit();
        if (timeLimit < 0) {
            return;
        }

        // 有更新topic权限的人 不受到时间限制
        if (StpUtil.hasPermission(PermissionConstant.TOPIC_UPDATE)) {
            return;
        }

        if (this.timeRangeType().isStrictTime()) {
            long useTime = Math.abs(ChronoUnit.MINUTES.between(LocalDateTime.now(), startTime));
            if (useTime > timeLimit) {
                String topicName = this.topicType().getName();
                String timeRange = this.timeRangeType().getName();
                throw new BusinessException("[%s]用时已经超过[%s] 且[%s]类型为[%s],无法继续查看[%s] or 提交题目".formatted(topicName,
                        timeLimit,
                        topicName,
                        timeRange,
                        topicName));
            }
        }
    }
}