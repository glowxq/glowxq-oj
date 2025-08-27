package com.glowxq.system.meta.pojo.dto;

import cn.idev.excel.annotation.ExcelProperty;
import com.glowxq.core.common.entity.base.BaseDTO;
import com.glowxq.core.util.BeanCopyUtils;
import com.glowxq.system.meta.pojo.po.MetaRegion;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
/**
 * <p>
 * MetaRegion导入DTO
 * </p>
 *
 * @author glowxq
 * @since 2025-06-20
 */
@Data
@Schema(description = "MetaRegion导入DTO")
public class MetaRegionImportDTO implements BaseDTO {

    @ExcelProperty(value = "父编码")
    @Schema(description =  "父编码")
    private Long parentId;

    @ExcelProperty(value = "祖级列表")
    @Schema(description =  "祖级列表")
    private String ancestors;

    @ExcelProperty(value = "地址名")
    @Schema(description =  "地址名")
    private String name;

    @ExcelProperty(value = "地址拼音")
    @Schema(description =  "地址拼音")
    private String pinyin;

    @ExcelProperty(value = "拼音前缀")
    @Schema(description =  "拼音前缀")
    private String pinyinPrefix;

    @ExcelProperty(value = "地址等级")
    @Schema(description =  "地址等级")
    private Integer level;

    @ExcelProperty(value = "启用")
    @Schema(description =  "启用")
    private Boolean enable;

    @ExcelProperty(value = "租户ID")
    @Schema(description =  "租户ID")
    private String tenantId;

    @Override
    public MetaRegion buildEntity() {
        return BeanCopyUtils.copy(this, MetaRegion.class);
    }
}