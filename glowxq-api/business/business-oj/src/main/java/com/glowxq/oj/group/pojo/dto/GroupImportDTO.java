package com.glowxq.oj.group.pojo.dto;

import cn.idev.excel.annotation.ExcelProperty;
import com.glowxq.core.common.entity.base.BaseDTO;
import com.glowxq.core.util.BeanCopyUtils;
import com.glowxq.oj.group.pojo.po.Group;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * <p>
 * Group导入DTO
 * </p>
 *
 * @author glowxq
 * @since 2025-03-27
 */
@Data
@Schema(description = "Group导入DTO")
public class GroupImportDTO implements BaseDTO {

    @ExcelProperty(value = "班级名")
    @Schema(description =  "班级名")
    private String name;

    @ExcelProperty(value = "班级代码")
    @Schema(description =  "班级代码")
    private String code;

    @ExcelProperty(value = "负责人ID")
    @Schema(description =  "负责人ID")
    private String principalUserId;

    @ExcelProperty(value = "负责人姓名")
    @Schema(description =  "负责人姓名")
    private String principalName;

    @ExcelProperty(value = "班级描述")
    @Schema(description =  "班级描述")
    private String description;

    @ExcelProperty(value = "班级颜色")
    @Schema(description =  "班级颜色")
    private String color;

    @ExcelProperty(value = "字体颜色")
    @Schema(description =  "字体颜色")
    private String textColor;

    @ExcelProperty(value = "启用")
    @Schema(description =  "启用")
    private Boolean enable;

    @Override
    public Group buildEntity() {
        return BeanCopyUtils.copy(this, Group.class);
    }
}