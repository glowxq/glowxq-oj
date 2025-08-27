package com.glowxq.oj.judge.pojo.dto;

import com.glowxq.core.common.entity.PageQuery;
import com.glowxq.core.common.valid.annotation.InEnum;
import com.glowxq.oj.judge.enums.JudgeSceneType;
import com.glowxq.oj.judge.enums.JudgeStatus;
import com.glowxq.oj.problem.enums.ProblemType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * <p>
 * Judge查询DTO
 * </p>
 *
 * @author glowxq
 * @since 2025-03-17
 */
@Data
@Schema(description = "Judge查询DTO")
public class JudgeListDTO extends PageQuery {

    @Schema(description = "题目ID")
    private Long problemId;

    @Schema(description = "题目KEY")
    private String problemKey;

    @Schema(description = "题目标题")
    private String problemTitle;

    @Schema(description = "测评人姓名")
    private String name;

    @Schema(description = "查询自己的测评")
    private Boolean myJudge;

    @Schema(description = "业务ID")
    private Long businessId;

    @Schema(description = "测评类型")
    @InEnum(enumClass = JudgeSceneType.class)
    private String sceneType;

    @Schema(description = "题目类型")
    @InEnum(enumClass = ProblemType.class)
    private String problemType;

    @Schema(description = "结果码具体参考文档")
    @InEnum(enumClass = JudgeStatus.class)
    private Integer status;

    @Schema(description = "是否为人工评测")
    private Boolean manualEvaluation;

    private List<Long> groupIds;

    public boolean isMyJudge() {
        return Boolean.TRUE.equals(myJudge);
    }
}