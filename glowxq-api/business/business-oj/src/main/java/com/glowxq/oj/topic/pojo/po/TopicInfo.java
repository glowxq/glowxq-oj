package com.glowxq.oj.topic.pojo.po;

import com.glowxq.core.common.entity.base.BaseEntity;
import com.glowxq.mysql.EntityChangeListener;
import com.glowxq.oj.topic.enums.TopicInfoStatus;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * <p>
 * 主题数据
 * </p>
 *
 * @author glowxq
 * @since 2025-04-12
 */
@Data
@Table(value = "topic_info", onInsert = EntityChangeListener.class, onUpdate = EntityChangeListener.class)
@Schema(description = "主题数据")
public class TopicInfo implements BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    @Schema(description = "")
    private Long id;

    @Schema(description = "主题ID")
    private Long topicId;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "头像")
    private String avatar;

    @Schema(description = "姓名")
    private String name;

    @Schema(description = "昵称")
    private String nickName;

    @Schema(description = "总得分")
    private Integer score;

    @Schema(description = "总AC数量")
    private Integer acNum;

    @Schema(description = "提交次数")
    private Integer submitNum;

    @Schema(description = "总做题用时(分)")
    private Integer useTime;

    @Schema(description = "总罚时(分)")
    private Integer punishmentTime;

    @Schema(description = "主题状态")
    private String status;

    @Schema(description = "开始时间")
    private LocalDateTime startTime;

    @Schema(description = "完成时间")
    private LocalDateTime endTime;

    @Schema(description = "自动提交")
    private Boolean autoSubmit;

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

    public TopicInfoStatus status() {
        return TopicInfoStatus.matchCode(status);
    }

    public void plusAcNum(Boolean ac) {
        if (ac) {
            this.acNum++;
        }
    }

    public void plusSubmitNum() {
        this.submitNum++;
    }

    public void plusScore(Integer score) {
        this.score += Optional.ofNullable(score).orElse(0);
    }

    public void plusUseTime(Integer useTime) {
        this.useTime += Optional.ofNullable(useTime).orElse(0);
    }

    public void plusPunishmentTime(Integer punishmentTime) {
        this.punishmentTime += punishmentTime;
    }


}