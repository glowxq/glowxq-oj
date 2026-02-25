package com.glowxq.oj.group.pojo.dto;

import com.glowxq.core.common.entity.base.BaseDTO;
import com.glowxq.core.util.BeanCopyUtils;
import com.glowxq.oj.group.pojo.po.Group;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * <p>
 * Group添加DTO
 * </p>
 *
 * @author glowxq
 * @since 2025-03-27
 */
@Data
@Schema(description = "Group添加DTO")
public class GroupCreateDTO implements BaseDTO {

    @Schema(description = "班级名")
    private String name;

    @Schema(description = "班级代码")
    private String code;

    @Schema(description = "负责人ID")
    @NotNull(message = "负责人必填")
    private String principalUserId;

    @Schema(description = "负责人姓名")
    private String principalName;

    @Schema(description = "班级描述")
    private String description;

    @Schema(description = "班级颜色")
    private String color;

    @Schema(description = "字体颜色")
    private String textColor;

    @Schema(description = "启用")
    private Boolean enable;

    @Schema(description = "标签")
    private List<Long> tagIds;
    @Override
    public Group buildEntity() {
        return BeanCopyUtils.copy(this, Group.class);
    }
}