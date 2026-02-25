package com.glowxq.system.admin.pojo.po;

import com.glowxq.core.common.entity.base.BaseEntity;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
/**
 * <p>
 * 系统数据角色-菜单表
 * </p>
 *
 * @author glowxq-admin
 * @since 2024-07-11
 */
@Data
@Table(value = "sys_data_role_menu")
@Schema(description = "系统数据角色-菜单表")
public class SysDataRoleMenu implements BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    @Schema(description = "ID")
    private Long id;

    @Schema(description = "sys_data_role_id （数据角色表）")
    private Long roleId;

    @Schema(description = "sys_menu_id （菜单表）")
    private String menuId;

}