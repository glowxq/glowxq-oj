package com.glowxq.system.admin.pojo.vo.sysmenu;

import com.glowxq.core.common.service.Treeable;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * SysMenuVO
 *
 * @author glowxq
 * @since 2023/8/21
 */
@Data
public class MenuTreeVO implements Treeable<MenuTreeVO> {

    @Schema(description = "id")
    private Object id;

    @Schema(description = "pid")
    private Object pid;

    @Schema(description = "层级")
    private Long deep;

    @Schema(description = "排序")
    private Long sort;

    @Schema(description = "title")
    private String title;

    @Schema(description = "菜单类型")
    private String type;

    @Schema(description = "菜单类型")
    private String menuTypeCd;

    @Schema(description = "按钮权限")
    private String permissions;

    @Schema(description = "路由名称")
    private String name;

    @Schema(description = "子级")
    private List<MenuTreeVO> children;
}
