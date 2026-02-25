package com.glowxq.system.meta.pojo.dto;

import com.glowxq.core.common.entity.base.BaseDTO;
import com.glowxq.core.util.BeanCopyUtils;
import com.glowxq.system.meta.pojo.po.MetaRegion;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * <p>
 * MetaRegion添加DTO
 * </p>
 *
 * @author glowxq
 * @since 2025-06-20
 */
@Data
@Schema(description = "MetaRegion添加DTO")
public class MetaRegionCreateDTO implements BaseDTO {

   @Schema(description =  "父编码")
   private Long parentId;

   @Schema(description =  "祖级列表")
   private String ancestors;

   @Schema(description =  "地址名")
   private String name;

   @Schema(description =  "地址拼音")
   private String pinyin;

   @Schema(description =  "拼音前缀")
   private String pinyinPrefix;

   @Schema(description =  "地址等级")
   private Integer level;

   @Schema(description =  "启用")
   private Boolean enable;

   @Schema(description =  "租户ID")
   private String tenantId;

    @Override
    public MetaRegion buildEntity() {
        return BeanCopyUtils.copy(this, MetaRegion.class);
    }
}