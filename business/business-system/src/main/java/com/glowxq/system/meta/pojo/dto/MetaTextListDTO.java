package com.glowxq.system.meta.pojo.dto;

import com.glowxq.core.common.entity.PageQuery;
import com.glowxq.core.common.entity.base.BaseDTO;
import com.glowxq.core.util.BeanCopyUtils;
import com.glowxq.system.meta.pojo.po.MetaText;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
/**
 * <p>
 * MetaText查询DTO
 * </p>
 *
 * @author glowxq
 * @since 2025-04-23
 */
@Data
@Schema(description = "MetaText查询DTO")
public class MetaTextListDTO extends PageQuery implements BaseDTO {

    @Schema(description =  "text名字")
    private String name;

    @Schema(description =  "文本key")
    private String textKey;

    @Schema(description =  "文本类型")
    private String textType;

    @Schema(description =  "图标")
    private String icon;

    @Schema(description =  "业务类型")
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