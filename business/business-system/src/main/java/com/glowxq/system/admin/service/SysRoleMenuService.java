package com.glowxq.system.admin.service;

import com.glowxq.system.admin.pojo.dto.sysrolemenu.SysRoleMenuDTO;
import com.glowxq.system.admin.pojo.po.SysRoleMenu;
import com.glowxq.system.admin.pojo.vo.sysrolemenu.SysRoleMenuVO;
import com.mybatisflex.core.service.IService;

/**
 * <p>
 * 系统角色-菜单表 服务类
 * </p>
 *
 * @author glowxq
 * @since 2022-10-01
 */
public interface SysRoleMenuService extends IService<SysRoleMenu> {

    void change(SysRoleMenuDTO dto);

    SysRoleMenuVO queryRoleMenu(Long roleId);
}
