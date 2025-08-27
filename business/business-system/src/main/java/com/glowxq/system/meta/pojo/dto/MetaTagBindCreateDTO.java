package com.glowxq.system.meta.pojo.dto;

import com.glowxq.core.common.entity.base.BaseDTO;
import com.glowxq.core.util.BeanCopyUtils;
import com.glowxq.system.meta.pojo.po.MetaTagBind;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * <p>
 * MetaTagBind添加DTO
 * </p>
 *
 * @author glowxq
 * @since 2025-04-23
 */
@Data
@Schema(description = "MetaTagBind添加DTO")
public class MetaTagBindCreateDTO implements BaseDTO {

   @Schema(description =  "标签ID")
   private Long tagId;

   @Schema(description =  "标签绑定的业务ID")
   private Long businessId;

   @Schema(description =  "绑定类型")
   private String type;

    @Override
    public MetaTagBind buildEntity() {
        return BeanCopyUtils.copy(this, MetaTagBind.class);
    }
}