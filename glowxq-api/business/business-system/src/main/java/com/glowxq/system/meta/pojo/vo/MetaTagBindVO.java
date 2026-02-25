package com.glowxq.system.meta.pojo.vo;

import cn.idev.excel.annotation.ExcelIgnore;
import cn.idev.excel.annotation.ExcelProperty;
import com.glowxq.core.common.entity.base.BaseVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * <p>
 * MetaTagBind返回vo
 * </p>
 *
 * @author glowxq
 * @since 2025-04-23
 */
@Data
@Schema(description = "MetaTagBind返回vo")
public class MetaTagBindVO implements BaseVO{

    @ExcelIgnore
    @Schema(description =  "")
    private Long id;

    @ExcelProperty(value = "标签ID")
    @Schema(description =  "标签ID")
    private Long tagId;

    @ExcelProperty(value = "标签绑定的业务ID")
    @Schema(description =  "标签绑定的业务ID")
    private Long businessId;

    @ExcelProperty(value = "绑定类型")
    @Schema(description =  "绑定类型")
    private String type;

}