package com.glowxq.system.admin.service;

import com.glowxq.system.admin.pojo.dto.sysmenu.SysUserRoleDTO;
import com.glowxq.system.admin.pojo.po.SysUserDataRole;
import com.glowxq.system.admin.pojo.vo.sysuser.SysUserRoleVO;
import com.mybatisflex.core.service.IService;

/**
 * <p>
 * 系统用户-数据角色关联表 Service
 * </p>
 *
 * @author glowxq-admin
 * @since 2024-07-11
 */
public interface SysUserDataRoleService extends IService<SysUserDataRole> {

    void changeRole(SysUserRoleDTO dto);

    SysUserRoleVO queryRoleMenu(Long userId);
}
