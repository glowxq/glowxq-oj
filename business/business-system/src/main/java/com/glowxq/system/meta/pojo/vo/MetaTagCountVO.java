package com.glowxq.system.meta.pojo.vo;

import cn.idev.excel.annotation.ExcelIgnore;
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
public class MetaTagCountVO implements BaseVO {

    @ExcelIgnore
    @Schema(description = "数量")
    private Long count;

    @Schema(description = "分类ID")
    private Long categoryId;
}