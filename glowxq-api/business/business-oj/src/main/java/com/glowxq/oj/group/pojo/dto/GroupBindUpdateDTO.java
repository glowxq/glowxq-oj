package com.glowxq.oj.group.pojo.dto;

import com.glowxq.core.common.entity.base.BaseDTO;
import com.glowxq.core.util.BeanCopyUtils;
import com.glowxq.oj.group.pojo.po.GroupBind;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * <p>
 * GroupBind修改DTO
 * </p>
 *
 * @author glowxq
 * @since 2025-03-28
 */
@Data
@Schema(description = "GroupBind修改DTO")
public class GroupBindUpdateDTO implements BaseDTO {

    @Schema(description =  "")
    private Long id;

    @Schema(description =  "班级ID")
    private Long groupId;

    @Schema(description =  "班级绑定的业务ID")
    private Long businessId;

    @Schema(description =  "绑定类型")
    private String type;

    @Override
    public GroupBind buildEntity() {
        return BeanCopyUtils.copy(this, GroupBind.class);
    }
}