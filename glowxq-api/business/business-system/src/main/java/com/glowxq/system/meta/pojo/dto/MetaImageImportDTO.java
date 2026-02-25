package com.glowxq.system.meta.pojo.dto;

import cn.idev.excel.annotation.ExcelProperty;
import com.glowxq.core.common.entity.base.BaseDTO;
import com.glowxq.core.util.BeanCopyUtils;
import com.glowxq.system.meta.pojo.po.MetaImage;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
/**
 * <p>
 * MetaImage导入DTO
 * </p>
 *
 * @author glowxq
 * @since 2025-04-25
 */
@Data
@Schema(description = "MetaImage导入DTO")
public class MetaImageImportDTO implements BaseDTO {

    @ExcelProperty(value = "图片名字")
    @Schema(description =  "图片名字")
    private String name;

    @ExcelProperty(value = "图片key")
    @Schema(description =  "图片key")
    private String imageKey;

    @ExcelProperty(value = "业务类型")
    @Schema(description =  "业务类型")
    private String businessType;

    @ExcelProperty(value = "图片URL")
    @Schema(description =  "图片URL")
    private String url;

    @ExcelProperty(value = "图片介绍")
    @Schema(description =  "图片介绍")
    private String content;

    @ExcelProperty(value = "跳转链接")
    @Schema(description =  "跳转链接")
    private String skipUrl;

    @ExcelProperty(value = "排序(降序)")
    @Schema(description =  "排序(降序)")
    private Integer sort;

    @ExcelProperty(value = "启用")
    @Schema(description =  "启用")
    private Boolean enable;

    @Override
    public MetaImage buildEntity() {
        return BeanCopyUtils.copy(this, MetaImage.class);
    }
}