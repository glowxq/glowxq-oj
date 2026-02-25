package com.glowxq.oj.group.pojo.dto;

import com.glowxq.core.common.entity.PageQuery;
import com.glowxq.core.common.entity.base.BaseDTO;
import com.glowxq.core.util.BeanCopyUtils;
import com.glowxq.oj.group.pojo.po.Group;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * <p>
 * Group查询DTO
 * </p>
 *
 * @author glowxq
 * @since 2025-03-27
 */
@Data
@Schema(description = "Group查询DTO")
public class GroupListDTO extends PageQuery implements BaseDTO {

    @Schema(description =  "班级名")
    private String name;

    @Schema(description =  "班级代码")
    private String code;

    @Schema(description =  "负责人ID")
    private String principalUserId;

    @Schema(description =  "负责人姓名")
    private String principalName;

    @Schema(description =  "班级描述")
    private String description;

    @Schema(description =  "班级颜色")
    private String color;

    @Schema(description =  "字体颜色")
    private String textColor;

    @Schema(description =  "启用")
    private Boolean enable;

    @Override
    public Group buildEntity() {
        return BeanCopyUtils.copy(this, Group.class);
    }
}