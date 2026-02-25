package com.glowxq.system.admin.service;

import com.glowxq.core.common.entity.PageResult;
import com.glowxq.core.common.entity.SelectIdsDTO;
import com.glowxq.system.admin.pojo.dto.sysdatarole.SysDataRoleCreateDTO;
import com.glowxq.system.admin.pojo.dto.sysdatarole.SysDataRoleListDTO;
import com.glowxq.system.admin.pojo.dto.sysdatarole.SysDataRoleUpdateDTO;
import com.glowxq.system.admin.pojo.po.SysDataRole;
import com.glowxq.system.admin.pojo.vo.sysdatarole.SysDataRoleMenuVO;
import com.glowxq.system.admin.pojo.vo.sysdatarole.SysDataRoleVO;
import com.mybatisflex.core.service.IService;

import java.util.List;

/**
 * <p>
 * 数据权限管理 Service
 * </p>
 *
 * @author glowxq-admin
 * @since 2024-07-09
 */
public interface SysDataRoleService extends IService<SysDataRole> {

    void create(SysDataRoleCreateDTO dto);

    void update(SysDataRoleUpdateDTO dto);

    PageResult<SysDataRoleVO> page(SysDataRoleListDTO dto);

    List<SysDataRoleVO> list(SysDataRoleListDTO dto);

    void remove(SelectIdsDTO dto);

    SysDataRoleVO detail(Object id);

    SysDataRoleMenuVO queryDataRoleMenu();
}
