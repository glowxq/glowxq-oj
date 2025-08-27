package com.glowxq.system.admin.mapper;

import com.glowxq.system.admin.pojo.po.SysRoleMenu;
import com.mybatisflex.core.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 系统角色-菜单表 Mapper 接口
 * </p>
 *
 * @author glowxq
 * @since 2023-08-21
 */
public interface SysRoleMenuMapper extends BaseMapper<SysRoleMenu> {

    void insertBatchSysRoleMenu(@Param("menuIds") List<String> menuIds, @Param("roleId") Long roleId);

}
