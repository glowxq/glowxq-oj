package com.glowxq.system.meta.pojo.vo;

import cn.idev.excel.annotation.ExcelIgnore;
import cn.idev.excel.annotation.ExcelProperty;
import com.glowxq.core.common.entity.base.BaseVO;
import com.glowxq.core.common.service.Treeable;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * <p>
 * MetaCategory返回vo
 * </p>
 *
 * @author glowxq
 * @since 2025-04-23
 */
@Data
@Schema(description = "MetaCategory返回vo")
public class MetaCategoryTreeVO implements BaseVO, Treeable<MetaCategoryTreeVO> {

    @ExcelIgnore
    @Schema(description = "分类ID")
    private Long id;

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

    @ExcelProperty(value = "是否锁定")
    @Schema(description = "是否锁定")
    private Boolean lock;

    @ExcelProperty(value = "备注")
    @Schema(description = "备注")
    private String remark;

    @Schema(description = "子级")
    private List<MetaCategoryTreeVO> children;
}