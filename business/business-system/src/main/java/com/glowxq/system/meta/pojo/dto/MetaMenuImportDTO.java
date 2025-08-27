package com.glowxq.system.meta.pojo.dto;

import cn.idev.excel.annotation.ExcelProperty;
import com.glowxq.core.common.entity.base.BaseDTO;
import com.glowxq.core.util.BeanCopyUtils;
import com.glowxq.system.meta.pojo.po.MetaMenu;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
/**
 * <p>
 * MetaMenu导入DTO
 * </p>
 *
 * @author glowxq
 * @since 2025-04-23
 */
@Data
@Schema(description = "MetaMenu导入DTO")
public class MetaMenuImportDTO implements BaseDTO {

    @ExcelProperty(value = "底部菜单名称")
    @Schema(description =  "底部菜单名称")
    private String name;

    @ExcelProperty(value = "选中图标")
    @Schema(description =  "选中图标")
    private String activeIcon;

    @ExcelProperty(value = "未选中图标")
    @Schema(description =  "未选中图标")
    private String inactiveIcon;

    @ExcelProperty(value = "图标类型")
    @Schema(description =  "图标类型")
    private String iconType;

    @ExcelProperty(value = "菜单类型")
    @Schema(description =  "菜单类型")
    private String type;

    @ExcelProperty(value = "菜单路径")
    @Schema(description =  "菜单路径")
    private String path;

    @ExcelProperty(value = "层级")
    @Schema(description =  "层级")
    private Boolean enable;

    @ExcelProperty(value = "排序")
    @Schema(description =  "排序")
    private Integer sort;

    @ExcelProperty(value = "是否有子级")
    @Schema(description =  "是否有子级")
    private Boolean hasChildren;

    @ExcelProperty(value = "是否锁定")
    @Schema(description =  "是否锁定")
    private Boolean lock;

    @ExcelProperty(value = "备注")
    @Schema(description =  "备注")
    private String remark;

    @Override
    public MetaMenu buildEntity() {
        return BeanCopyUtils.copy(this, MetaMenu.class);
    }
}