package com.glowxq.system.admin.service.impl;

import com.glowxq.system.admin.mapper.SysUserRoleMapper;
import com.glowxq.system.admin.pojo.po.SysUserRole;
import com.glowxq.system.admin.service.SysUserRoleService;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.glowxq.system.admin.pojo.po.table.SysUserRoleTableDef.SYS_USER_ROLE;

/**
 * <p>
 * 系统用户-角色关联表 服务实现类
 * </p>
 *
 * @author glowxq
 * @since 2022-10-01
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SysUserRoleServiceImpl extends ServiceImpl<SysUserRoleMapper, SysUserRole> implements SysUserRoleService {

    /**
     * 获取用户的角色
     * 
     * @param userId
     *            用户id
     * @return 用户角色集合
     */
    @Override
    public List<String> getUserRolesByUserId(Long userId) {
        QueryWrapper queryWrapper = QueryWrapper.create().select(SYS_USER_ROLE.ROLE_ID).from(SYS_USER_ROLE).where(SYS_USER_ROLE.USER_ID.eq(userId));
        return listAs(queryWrapper, String.class);
    }

    @Override
    public List<String> getUserRoleKeysByUserId(Long userId) {
        QueryWrapper queryWrapper = QueryWrapper.create().select(SYS_USER_ROLE.ROLE_ID).from(SYS_USER_ROLE).where(SYS_USER_ROLE.USER_ID.eq(userId));
        return listAs(queryWrapper, String.class);
    }
}