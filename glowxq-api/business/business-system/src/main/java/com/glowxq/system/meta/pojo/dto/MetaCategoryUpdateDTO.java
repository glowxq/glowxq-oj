package com.glowxq.system.meta.pojo.dto;

import com.glowxq.core.common.entity.base.BaseDTO;
import com.glowxq.core.util.BeanCopyUtils;
import com.glowxq.system.meta.pojo.po.MetaCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * <p>
 * MetaCategory修改DTO
 * </p>
 *
 * @author glowxq
 * @since 2025-04-23
 */
@Data
@Schema(description = "MetaCategory修改DTO")
public class MetaCategoryUpdateDTO implements BaseDTO {

    @Schema(description =  "分类ID")
    private Long id;

    @Schema(description =  "分类名称")
    private String name;

    @Schema(description =  "父级ID")
    private Long pid;

    @Schema(description =  "层级")
    private Integer deep;

    @Schema(description =  "排序")
    private Integer sort;

    @Schema(description =  "启用")
    private Boolean enable;

    @Schema(description =  "是否有子级")
    private Boolean hasChildren;

    @Schema(description =  "是否锁定")
    private Boolean lock;

    @Schema(description =  "备注")
    private String remark;

    @Override
    public MetaCategory buildEntity() {
        return BeanCopyUtils.copy(this, MetaCategory.class);
    }
}