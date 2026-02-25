package com.glowxq.system.meta.pojo.dto;

import cn.idev.excel.annotation.ExcelProperty;
import com.glowxq.core.common.entity.base.BaseDTO;
import com.glowxq.core.util.BeanCopyUtils;
import com.glowxq.system.meta.pojo.po.MetaText;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
/**
 * <p>
 * MetaText导入DTO
 * </p>
 *
 * @author glowxq
 * @since 2025-04-23
 */
@Data
@Schema(description = "MetaText导入DTO")
public class MetaTextImportDTO implements BaseDTO {

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

    @Override
    public MetaText buildEntity() {
        return BeanCopyUtils.copy(this, MetaText.class);
    }
}