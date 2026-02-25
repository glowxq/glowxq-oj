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
 * 系统用户-数据角色关联表
 * </p>
 *
 * @author glowxq-admin
 * @since 2024-07-11
 */
@Data
@Table(value = "sys_user_data_role")
@Schema(description = "系统用户-数据角色关联表")
public class SysUserDataRole implements BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    @Schema(description = "ID")
    private Long id;

    @Schema(description = "数据角色id (sys_data_role_id)")
    private Long roleId;

    @Schema(description = "用户id(sys_user_id)")
    private Long userId;

}