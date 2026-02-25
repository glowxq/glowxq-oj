package com.glowxq.system.meta.pojo.vo;

import cn.idev.excel.annotation.ExcelIgnore;
import cn.idev.excel.annotation.ExcelProperty;
import com.glowxq.core.common.entity.base.BaseVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * <p>
 * MetaText返回vo
 * </p>
 *
 * @author glowxq
 * @since 2025-04-23
 */
@Data
@Schema(description = "MetaText返回vo")
public class MetaTextVO implements BaseVO{

    @ExcelIgnore
    @Schema(description =  "")
    private Long id;

    @ExcelProperty(value = "text名字")
    @Schema(description =  "text名字")
    private String name;

    @ExcelProperty(value = "文本key")
    @Schema(description =  "文本key")
    private String textKey;

    @ExcelProperty(value = "文本类型")
    @Schema(description =  "文本类型")
    private String textType;

    @ExcelProperty(value = "图标")
    @Schema(description =  "图标")
    private String icon;

    @ExcelProperty(value = "业务类型")
    @Schema(description =  "业务类型")
    private String businessType;

    @ExcelProperty(value = "文本标题")
    @Schema(description =  "文本标题")
    private String title;

    @ExcelProperty(value = "跳转URL")
    @Schema(description =  "跳转URL")
    private String skipUrl;

    @ExcelProperty(value = "文本内容")
    @Schema(description =  "文本内容")
    private String content;

    @ExcelProperty(value = "排序(降序)")
    @Schema(description =  "排序(降序)")
    private Integer sort;

    @ExcelProperty(value = "启用")
    @Schema(description =  "启用")
    private Boolean enable;

}