package com.glowxq.system.admin.service;

import com.glowxq.core.common.entity.PageResult;
import com.glowxq.core.common.entity.SelectIdsDTO;
import com.glowxq.system.admin.pojo.dto.sysrole.SysRoleCreateDTO;
import com.glowxq.system.admin.pojo.dto.sysrole.SysRoleListDTO;
import com.glowxq.system.admin.pojo.dto.sysrole.SysRoleUpdateDTO;
import com.glowxq.system.admin.pojo.po.SysRole;
import com.mybatisflex.core.service.IService;

import java.util.List;

/**
 * <p>
 * 系统角色表 服务类
 * </p>
 *
 * @author glowxq
 * @since 2022-10-01
 */
public interface SysRoleService extends IService<SysRole> {

    void create(SysRoleCreateDTO dto);

    void update(SysRoleUpdateDTO dto);

    void remove(SelectIdsDTO dto);

    void removeByMenuId(SelectIdsDTO dto);

    PageResult<SysRole> list(SysRoleListDTO dto);

    List<SysRole> listByUserId(Long id);

    SysRole getByPerm(String member);
}
