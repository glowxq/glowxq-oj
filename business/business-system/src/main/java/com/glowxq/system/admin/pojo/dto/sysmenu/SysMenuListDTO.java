package com.glowxq.system.admin.pojo.dto.sysmenu;

import com.glowxq.core.common.valid.annotation.InEnum;
import com.glowxq.system.admin.enums.MenuMode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * SysMenuQueryDTO
 *
 * @author glowxq
 * @since 2023/8/21
 */
@Data
public class SysMenuListDTO {

    @Schema(description = "是否查询按钮")
    private boolean isShowButton = true;

    /**
     * 菜单类型
     */
    @InEnum(enumClass = MenuMode.class)
    private String type;
}
