package com.glowxq.oj.topic.pojo.dto;

import com.glowxq.core.common.entity.PageQuery;
import com.glowxq.core.common.entity.base.BaseDTO;
import com.glowxq.core.util.BeanCopyUtils;
import com.glowxq.oj.topic.pojo.po.TopicSubmit;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * <p>
 * TopicSubmit查询DTO
 * </p>
 *
 * @author glowxq
 * @since 2025-04-12
 */
@Data
@Schema(description = "TopicSubmit查询DTO")
public class TopicSubmitListDTO extends PageQuery implements BaseDTO {

    @Schema(description =  "主题ID")
    private Long topicId;

    @Schema(description =  "题目ID")
    private Long problemId;

    @Schema(description =  "题目Key")
    private String problemKey;

    @Schema(description =  "题目标题ID")
    private String problemTitle;

    @Schema(description =  "题目类型")
    private String problemType;

    @Schema(description =  "主题测评类型 ACM/OI")
    private Integer topicJudgeType;

    @Schema(description =  "用户ID")
    private Long userId;

    @Schema(description =  "姓名")
    private String name;

    @Schema(description =  "昵称")
    private String nickName;

    @Schema(description =  "测评状态")
    private Integer judgeStatus;

    @Schema(description =  "主题分数")
    private Integer score;

    @Schema(description =  "做题用时(分)")
    private Integer useTime;

    @Schema(description =  "罚时(分)")
    private Integer punishmentTime;

    @Override
    public TopicSubmit buildEntity() {
        return BeanCopyUtils.copy(this, TopicSubmit.class);
    }
}