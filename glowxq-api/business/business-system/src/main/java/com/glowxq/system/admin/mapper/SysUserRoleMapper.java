package com.glowxq.system.admin.mapper;

import com.glowxq.system.admin.pojo.po.SysUserRole;
import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.query.QueryWrapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 系统用户-角色关联表 Mapper 接口
 * </p>
 *
 * @author glowxq
 * @since 2023-08-29
 */
public interface SysUserRoleMapper extends BaseMapper<SysUserRole> {

    void insertBatchSysUserRole(@Param("roleIds") List<Long> roleIds, @Param("userId") Long userId);

    List<String> queryMenuIdByUserId(@Param("userId") Long userId);

    List<String> queryPermissionByUserId(@Param("userId") Long userId);

    default List<SysUserRole> listByUserId(Long userId) {
        QueryWrapper qw = QueryWrapper.create();
        qw.eq(SysUserRole::getUserId, userId);
        return this.selectListByQuery(qw);
    }
}
