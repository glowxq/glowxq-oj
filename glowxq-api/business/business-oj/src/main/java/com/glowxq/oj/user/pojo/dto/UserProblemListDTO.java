package com.glowxq.oj.user.pojo.dto;

import com.glowxq.core.common.entity.PageQuery;
import com.glowxq.core.common.entity.base.BaseDTO;
import com.glowxq.core.util.BeanCopyUtils;
import com.glowxq.oj.user.pojo.po.UserProblem;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * <p>
 * UserProblem查询DTO
 * </p>
 *
 * @author glowxq
 * @since 2025-04-03
 */
@Data
@Schema(description = "UserProblem查询DTO")
public class UserProblemListDTO extends PageQuery implements BaseDTO {

    @Schema(description =  "用户ID")
    private Long userId;

    @Schema(description =  "题目ID")
    private Long problemId;

    @Schema(description =  "题目Key")
    private String problemKey;

    @Schema(description =  "题目标题ID")
    private String problemTitle;

    @Schema(description =  "测评ID")
    private Long judgeId;

    @Schema(description =  "测评状态")
    private Integer judgeStatus;

    @Schema(description =  "作业分数")
    private Integer score;

    @Schema(description =  "AC的代码")
    private String code;

    @Schema(description =  "非编程题作答内容")
    private String options;

    @Schema(description =  "流程图URL")
    private String flowImage;

    @Schema(description =  "题目类型")
    private String problemType;

    @Override
    public UserProblem buildEntity() {
        return BeanCopyUtils.copy(this, UserProblem.class);
    }
}