package com.glowxq.system.meta.pojo.dto;

import cn.idev.excel.annotation.ExcelProperty;
import com.glowxq.core.common.entity.base.BaseDTO;
import com.glowxq.core.util.BeanCopyUtils;
import com.glowxq.system.meta.pojo.po.MetaCategoryClosure;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
/**
 * <p>
 * MetaCategoryClosure导入DTO
 * </p>
 *
 * @author glowxq
 * @since 2025-04-24
 */
@Data
@Schema(description = "MetaCategoryClosure导入DTO")
public class MetaCategoryClosureImportDTO implements BaseDTO {

    @ExcelProperty(value = "祖先节点ID")
    @Schema(description =  "祖先节点ID")
    private Long ancestorId;

    @ExcelProperty(value = "后代节点ID")
    @Schema(description =  "后代节点ID")
    private Long descendantId;

    @ExcelProperty(value = "祖先节点到后代节点的距离")
    @Schema(description =  "祖先节点到后代节点的距离")
    private Integer depth;

    @Override
    public MetaCategoryClosure buildEntity() {
        return BeanCopyUtils.copy(this, MetaCategoryClosure.class);
    }
}