package com.glowxq.system.meta.pojo.dto;

import cn.idev.excel.annotation.ExcelProperty;
import com.glowxq.core.common.entity.base.BaseDTO;
import com.glowxq.core.util.BeanCopyUtils;
import com.glowxq.system.meta.pojo.po.MetaTagBind;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
/**
 * <p>
 * MetaTagBind导入DTO
 * </p>
 *
 * @author glowxq
 * @since 2025-04-23
 */
@Data
@Schema(description = "MetaTagBind导入DTO")
public class MetaTagBindImportDTO implements BaseDTO {

    @ExcelProperty(value = "标签ID")
    @Schema(description =  "标签ID")
    private Long tagId;

    @ExcelProperty(value = "标签绑定的业务ID")
    @Schema(description =  "标签绑定的业务ID")
    private Long businessId;

    @ExcelProperty(value = "绑定类型")
    @Schema(description =  "绑定类型")
    private String type;

    @Override
    public MetaTagBind buildEntity() {
        return BeanCopyUtils.copy(this, MetaTagBind.class);
    }
}