package com.glowxq.system.meta.pojo.dto;

import com.glowxq.core.common.entity.base.BaseDTO;
import com.glowxq.core.common.valid.annotation.InEnum;
import com.glowxq.core.util.BeanCopyUtils;
import com.glowxq.system.meta.enums.ImageBusinessType;
import com.glowxq.system.meta.pojo.po.MetaImage;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * <p>
 * MetaImage修改DTO
 * </p>
 *
 * @author glowxq
 * @since 2025-04-25
 */
@Data
@Schema(description = "MetaImage修改DTO")
public class MetaImageUpdateDTO implements BaseDTO {

    @Schema(description = "")
    private Long id;

    @Schema(description = "图片名字")
    private String name;

    @Schema(description = "图片key")
    private String imageKey;

    @Schema(description = "业务类型")
    @InEnum(enumClass = ImageBusinessType.class)
    private String businessType;

    @Schema(description = "图片URL")
    private String url;

    @Schema(description = "图片介绍")
    private String content;

    @Schema(description = "跳转链接")
    private String skipUrl;

    @Schema(description = "排序(降序)")
    private Integer sort;

    @Schema(description = "启用")
    private Boolean enable;

    @Override
    public MetaImage buildEntity() {
        return BeanCopyUtils.copy(this, MetaImage.class);
    }
}