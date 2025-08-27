package com.glowxq.system.admin.mapper;

import com.glowxq.system.admin.pojo.po.SysRole;
import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.query.QueryWrapper;

/**
 * <p>
 * 系统角色表 Mapper 接口
 * </p>
 *
 * @author glowxq
 * @since 2023-08-21
 */
public interface SysRoleMapper extends BaseMapper<SysRole> {

    default SysRole selectByKey(String roleName){
        QueryWrapper qw = QueryWrapper.create();
        qw.eq(SysRole::getPermissions, roleName);
        return this.selectOneByQuery(qw);
    }
}
