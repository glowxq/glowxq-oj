package com.glowxq.system.meta.pojo.dto;

import com.glowxq.core.common.entity.base.BaseDTO;
import com.glowxq.core.util.BeanCopyUtils;
import com.glowxq.system.meta.pojo.po.MetaTag;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * <p>
 * MetaTag添加DTO
 * </p>
 *
 * @author glowxq
 * @since 2025-04-23
 */
@Data
@Schema(description = "MetaTag添加DTO")
public class MetaTagCreateDTO implements BaseDTO {

    @Schema(description = "分类ID")
    private Long categoryId;

   @Schema(description =  "标签名字")
   private String name;

   @Schema(description =  "背景")
   private String backgroundColor;

   @Schema(description =  "字体颜色")
   private String textColor;

   @Schema(description =  "镂空样式")
   private Boolean plain;

   @Schema(description =  "启用")
   private Boolean enable;

    @Override
    public MetaTag buildEntity() {
        return BeanCopyUtils.copy(this, MetaTag.class);
    }
}