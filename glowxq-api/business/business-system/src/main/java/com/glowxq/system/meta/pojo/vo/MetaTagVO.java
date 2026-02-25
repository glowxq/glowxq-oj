package com.glowxq.system.meta.pojo.vo;

import cn.idev.excel.annotation.ExcelIgnore;
import cn.idev.excel.annotation.ExcelProperty;
import com.glowxq.core.common.entity.base.BaseVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * <p>
 * MetaTag返回vo
 * </p>
 *
 * @author glowxq
 * @since 2025-04-23
 */
@Data
@Schema(description = "MetaTag返回vo")
public class MetaTagVO implements BaseVO{

    @ExcelIgnore
    @Schema(description =  "")
    private Long id;

    @Schema(description = "分类ID")
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

}