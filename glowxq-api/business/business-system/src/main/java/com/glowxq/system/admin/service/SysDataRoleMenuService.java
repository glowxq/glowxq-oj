package com.glowxq.system.admin.service;

import com.glowxq.system.admin.pojo.po.SysDataRoleMenu;
import com.mybatisflex.core.service.IService;

import java.util.List;

/**
 * <p>
 * 系统数据角色-菜单表 Service
 * </p>
 *
 * @author glowxq-admin
 * @since 2024-07-11
 */
public interface SysDataRoleMenuService extends IService<SysDataRoleMenu> {

    void batchSave(Long roleId, List<String> menuIds);

    List<String> getSelectMenuIdByRoleId(Long roleId);
}
