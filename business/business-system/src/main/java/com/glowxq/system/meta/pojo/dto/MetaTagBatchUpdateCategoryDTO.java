package com.glowxq.system.meta.pojo.dto;

import com.glowxq.core.common.entity.base.BaseDTO;
import com.glowxq.core.util.BeanCopyUtils;
import com.glowxq.system.meta.pojo.po.MetaTag;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * <p>
 * MetaTag修改DTO
 * </p>
 *
 * @author glowxq
 * @since 2025-04-23
 */
@Data
@Schema(description = "MetaTag修改DTO")
public class MetaTagBatchUpdateCategoryDTO implements BaseDTO {

    @Schema(description = "")
    private List<Long> tagIds;

    @Schema(description = "分类ID")
    private Long categoryId;

    @Override
    public MetaTag buildEntity() {
        return BeanCopyUtils.copy(this, MetaTag.class);
    }
}