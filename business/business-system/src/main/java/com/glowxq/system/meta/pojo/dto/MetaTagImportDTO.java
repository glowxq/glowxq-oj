package com.glowxq.system.meta.pojo.dto;

import cn.idev.excel.annotation.ExcelProperty;
import com.glowxq.core.common.entity.base.BaseDTO;
import com.glowxq.core.util.BeanCopyUtils;
import com.glowxq.system.meta.pojo.po.MetaTag;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
/**
 * <p>
 * MetaTag导入DTO
 * </p>
 *
 * @author glowxq
 * @since 2025-04-23
 */
@Data
@Schema(description = "MetaTag导入DTO")
public class MetaTagImportDTO implements BaseDTO {

    @Schema(description ="分类ID")
    private Long categoryId;

    @ExcelProperty(value = "标签名字")
    @Schema(description =  "标签名字")
    private String name;

    @ExcelProperty(value = "背景")
    @Schema(description =  "背景")
    private String backgroundColor;

    @ExcelProperty(value = "字体颜色")
    @Schema(description =  "字体颜色")
    private String textColor;

    @ExcelProperty(value = "镂空样式")
    @Schema(description =  "镂空样式")
    private Boolean plain;

    @ExcelProperty(value = "启用")
    @Schema(description =  "启用")
    private Boolean enable;

    @Override
    public MetaTag buildEntity() {
        return BeanCopyUtils.copy(this, MetaTag.class);
    }
}