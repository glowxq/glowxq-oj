package com.glowxq.system.meta.pojo.dto;

import com.glowxq.core.common.entity.base.BaseDTO;
import com.glowxq.core.util.BeanCopyUtils;
import com.glowxq.system.meta.pojo.po.MetaTagCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * <p>
 * MetaTagCategory修改DTO
 * </p>
 *
 * @author glowxq
 * @since 2025-06-10
 */
@Data
@Schema(description = "MetaTagCategory修改DTO")
public class MetaTagCategoryUpdateDTO implements BaseDTO {

    @Schema(description = "分类ID")
    private Long id;

    @Schema(description = "分类名称")
    private String name;

    @Schema(description = "启用")
    private Boolean enable;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "租户ID")
    private String tenantId;

    @Override
    public MetaTagCategory buildEntity() {
        return BeanCopyUtils.copy(this, MetaTagCategory.class);
    }
}