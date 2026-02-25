package com.glowxq.system.meta.pojo.dto;

import cn.idev.excel.annotation.ExcelProperty;
import com.glowxq.core.common.entity.base.BaseDTO;
import com.glowxq.core.util.BeanCopyUtils;
import com.glowxq.system.meta.pojo.po.MetaCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * <p>
 * MetaCategory导入DTO
 * </p>
 *
 * @author glowxq
 * @since 2025-04-23
 */
@Data
@Schema(description = "MetaCategory导入DTO")
public class MetaCategoryImportDTO implements BaseDTO {

    @ExcelProperty(value = "分类名称")
    @Schema(description = "分类名称")
    private String name;

    @ExcelProperty(value = "父级ID")
    @Schema(description = "父级ID")
    private Long pid;

    @ExcelProperty(value = "层级")
    @Schema(description = "层级")
    private Long deep;

    @ExcelProperty(value = "排序")
    @Schema(description = "排序")
    private Long sort;

    @ExcelProperty(value = "启用")
    @Schema(description = "启用")
    private Boolean enable;

    @ExcelProperty(value = "是否有子级")
    @Schema(description = "是否有子级")
    private Boolean hasChildren;

    @ExcelProperty(value = "是否锁定")
    @Schema(description = "是否锁定")
    private Boolean lock;

    @ExcelProperty(value = "备注")
    @Schema(description = "备注")
    private String remark;

    @Override
    public MetaCategory buildEntity() {
        return BeanCopyUtils.copy(this, MetaCategory.class);
    }
}