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
public class RankingDTO extends PageQuery implements BaseDTO {

    @Schema(description = "班级id")
    private Long groupId;

    @Override
    public UserProblem buildEntity() {
        return BeanCopyUtils.copy(this, UserProblem.class);
    }
}