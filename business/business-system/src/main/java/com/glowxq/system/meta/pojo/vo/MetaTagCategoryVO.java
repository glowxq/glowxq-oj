package com.glowxq.system.meta.pojo.vo;

import cn.idev.excel.annotation.ExcelIgnore;
import cn.idev.excel.annotation.ExcelProperty;
import com.glowxq.core.common.entity.base.BaseVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * <p>
 * MetaTagCategory返回vo
 * </p>
 *
 * @author glowxq
 * @since 2025-06-10
 */
@Data
@Schema(description = "MetaTagCategory返回vo")
public class MetaTagCategoryVO implements BaseVO {

    @ExcelIgnore
    @Schema(description = "分类ID")
    private Long id;

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
}