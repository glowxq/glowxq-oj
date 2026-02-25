package com.glowxq.system.meta.pojo.vo;

import cn.idev.excel.annotation.ExcelProperty;
import com.glowxq.core.common.entity.base.BaseVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * <p>
 * MetaCategoryClosure返回vo
 * </p>
 *
 * @author glowxq
 * @since 2025-04-24
 */
@Data
@Schema(description = "MetaCategoryClosure返回vo")
public class MetaCategoryClosureVO implements BaseVO{

    @ExcelProperty(value = "祖先节点ID")
    @Schema(description =  "祖先节点ID")
    private Long ancestorId;

    @ExcelProperty(value = "后代节点ID")
    @Schema(description =  "后代节点ID")
    private Long descendantId;

    @ExcelProperty(value = "祖先节点到后代节点的距离")
    @Schema(description =  "祖先节点到后代节点的距离")
    private Integer depth;

}