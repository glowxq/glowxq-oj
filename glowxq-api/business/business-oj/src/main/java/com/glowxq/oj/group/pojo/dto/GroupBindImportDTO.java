package com.glowxq.oj.group.pojo.dto;

import cn.idev.excel.annotation.ExcelProperty;
import com.glowxq.core.common.entity.base.BaseDTO;
import com.glowxq.core.util.BeanCopyUtils;
import com.glowxq.oj.group.pojo.po.GroupBind;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * <p>
 * GroupBind导入DTO
 * </p>
 *
 * @author glowxq
 * @since 2025-03-28
 */
@Data
@Schema(description = "GroupBind导入DTO")
public class GroupBindImportDTO implements BaseDTO {

    @ExcelProperty(value = "班级ID")
    @Schema(description =  "班级ID")
    private Long groupId;

    @ExcelProperty(value = "班级绑定的业务ID")
    @Schema(description =  "班级绑定的业务ID")
    private Long businessId;

    @ExcelProperty(value = "绑定类型")
    @Schema(description =  "绑定类型")
    private String type;

    @Override
    public GroupBind buildEntity() {
        return BeanCopyUtils.copy(this, GroupBind.class);
    }
}