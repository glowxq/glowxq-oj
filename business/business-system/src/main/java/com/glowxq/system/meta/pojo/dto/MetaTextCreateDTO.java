package com.glowxq.system.meta.pojo.dto;

import com.glowxq.core.common.entity.base.BaseDTO;
import com.glowxq.core.common.valid.annotation.InEnum;
import com.glowxq.core.util.BeanCopyUtils;
import com.glowxq.system.meta.enums.TextBusinessType;
import com.glowxq.system.meta.enums.TextType;
import com.glowxq.system.meta.pojo.po.MetaText;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * <p>
 * MetaText添加DTO
 * </p>
 *
 * @author glowxq
 * @since 2025-04-23
 */
@Data
@Schema(description = "MetaText添加DTO")
public class MetaTextCreateDTO implements BaseDTO {

   @Schema(description ="分类ID")
   private Long categoryId;

   @Schema(description =  "text名字")
   private String name;

   @Schema(description =  "文本key")
   private String textKey;

   @Schema(description =  "文本类型")
   @InEnum(enumClass = TextType.class)
   private String textType;

   @Schema(description =  "图标")
   private String icon;

   @Schema(description =  "业务类型")
   @InEnum(enumClass = TextBusinessType.class)
   private String businessType;

   @Schema(description =  "文本标题")
   private String title;

   @Schema(description =  "跳转URL")
   private String skipUrl;

   @Schema(description =  "文本内容")
   private String content;

   @Schema(description =  "排序(降序)")
   private Integer sort;

   @Schema(description =  "启用")
   private Boolean enable;

    @Override
    public MetaText buildEntity() {
        return BeanCopyUtils.copy(this, MetaText.class);
    }
}