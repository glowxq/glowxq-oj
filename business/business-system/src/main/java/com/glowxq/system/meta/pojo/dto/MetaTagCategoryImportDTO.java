package com.glowxq.system.meta.pojo.dto;

import cn.idev.excel.annotation.ExcelProperty;
import com.glowxq.core.common.entity.base.BaseDTO;
import com.glowxq.core.util.BeanCopyUtils;
import com.glowxq.system.meta.pojo.po.MetaTagCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * <p>
 * MetaTagCategory导入DTO
 * </p>
 *
 * @author glowxq
 * @since 2025-06-10
 */
@Data
@Schema(description = "MetaTagCategory导入DTO")
public class MetaTagCategoryImportDTO implements BaseDTO {

    @ExcelProperty(value = "分类名称")
    @Schema(description = "分类名称")
    private String name;

    @ExcelProperty(value = "启用")
    @Schema(description = "启用")
    private Boolean enable;

    @ExcelProperty(value = "备注")
    @Schema(description = "备注")
    private String remark;

    @ExcelProperty(value = "租户ID")
    @Schema(description = "租户ID")
    private String tenantId;

    @Override
    public MetaTagCategory buildEntity() {
        return BeanCopyUtils.copy(this, MetaTagCategory.class);
    }
}