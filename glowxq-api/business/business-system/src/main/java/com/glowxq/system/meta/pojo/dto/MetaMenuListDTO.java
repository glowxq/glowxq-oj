package com.glowxq.system.meta.pojo.dto;

import com.glowxq.core.common.entity.PageQuery;
import com.glowxq.core.common.entity.base.BaseDTO;
import com.glowxq.core.util.BeanCopyUtils;
import com.glowxq.system.meta.pojo.po.MetaMenu;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
/**
 * <p>
 * MetaMenu查询DTO
 * </p>
 *
 * @author glowxq
 * @since 2025-04-23
 */
@Data
@Schema(description = "MetaMenu查询DTO")
public class MetaMenuListDTO extends PageQuery implements BaseDTO {

    @Schema(description =  "底部菜单名称")
    private String name;

    @Schema(description =  "选中图标")
    private String activeIcon;

    @Schema(description =  "未选中图标")
    private String inactiveIcon;

    @Schema(description =  "图标类型")
    private String iconType;

    @Schema(description =  "菜单类型")
    private String type;

    @Schema(description =  "菜单路径")
    private String path;

    @Schema(description =  "层级")
    private Boolean enable;

    @Schema(description =  "排序")
    private Integer sort;

    @Schema(description =  "是否有子级")
    private Boolean hasChildren;

    @Schema(description =  "是否锁定")
    private Boolean lock;

    @Override
    public MetaMenu buildEntity() {
        return BeanCopyUtils.copy(this, MetaMenu.class);
    }
}