package com.glowxq.system.admin.service.impl;

import com.glowxq.core.common.entity.PageResult;
import com.glowxq.core.common.entity.SelectIdsDTO;
import com.glowxq.core.common.enums.CommonResponseEnum;
import com.glowxq.core.util.BeanCopyUtils;
import com.glowxq.core.util.PageUtils;
import com.glowxq.core.util.Utils;
import com.glowxq.security.core.util.LoginUtils;
import com.glowxq.system.admin.mapper.SysRoleMapper;
import com.glowxq.system.admin.mapper.SysRoleMenuMapper;
import com.glowxq.system.admin.mapper.SysUserRoleMapper;
import com.glowxq.system.admin.pojo.dto.sysrole.SysRoleCreateDTO;
import com.glowxq.system.admin.pojo.dto.sysrole.SysRoleListDTO;
import com.glowxq.system.admin.pojo.dto.sysrole.SysRoleUpdateDTO;
import com.glowxq.system.admin.pojo.po.SysRole;
import com.glowxq.system.admin.pojo.po.SysRoleMenu;
import com.glowxq.system.admin.pojo.po.SysUserRole;
import com.glowxq.system.admin.service.SysRoleService;
import com.glowxq.tenant.utils.TenantUtils;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 系统角色表 服务实现类
 * </p>
 *
 * @author glowxq
 * @since 2022-10-01
 */
@Service
@RequiredArgsConstructor
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {

    private final SysRoleMenuMapper sysRoleMenuMapper;

    private final SysUserRoleMapper sysUserRoleMapper;

    @Override
    public void create(SysRoleCreateDTO dto) {
        SysRole sysRole = BeanCopyUtils.copy(dto, SysRole.class);
        long count;
        count = QueryChain.of(SysRole.class).eq(SysRole::getRoleName, dto.getRoleName()).count();
        CommonResponseEnum.EXISTS.message("roleName已存在").assertTrue(count > 0);
        if (Utils.isNotNull(dto.getPermissions())) {
            count = QueryChain.of(SysRole.class).eq(SysRole::getPermissions, dto.getPermissions()).count();
            CommonResponseEnum.EXISTS.message("permissions已存在").assertTrue(count > 0);
        }
        save(sysRole);
    }

    @Override
    public void update(SysRoleUpdateDTO dto) {
        SysRole sysRole = BeanCopyUtils.copy(dto, SysRole.class);
        long count;
        count = QueryChain.of(SysRole.class).eq(SysRole::getRoleName, dto.getRoleName()).ne(SysRole::getId, dto.getId()).count();
        CommonResponseEnum.EXISTS.message("roleName已存在").assertTrue(count > 0);
        if (Utils.isNotNull(dto.getPermissions())) {
            count = QueryChain.of(SysRole.class).eq(SysRole::getPermissions, dto.getPermissions()).ne(SysRole::getId, dto.getId()).count();
            CommonResponseEnum.EXISTS.message("permissions已存在").assertTrue(count > 0);
        }
        updateById(sysRole);
    }

    @Override
    public void remove(SelectIdsDTO dto) {
        removeByIds(dto.getIds());
    }

    @Override
    public void removeByMenuId(SelectIdsDTO dto) {
        QueryWrapper wrapper = QueryWrapper.create().in(SysRoleMenu::getMenuId, dto.getIds());
        sysRoleMenuMapper.deleteByQuery(wrapper);
    }

    @Override
    public PageResult<SysRole> list(SysRoleListDTO dto) {
        QueryWrapper wrapper = QueryWrapper.create();
        if (Utils.isNotNull(dto.getRoleName())) {
            wrapper.like(SysRole::getRoleName, dto.getRoleName());
        }
        if (!LoginUtils.isSuperAdmin() || !TenantUtils.isRootTenant()){

        }
        return PageUtils.getPageResult(page(PageUtils.getPage(dto), wrapper));
    }

    @Override
    public List<SysRole> listByUserId(Long userId) {
        List<SysUserRole> sysUserRoles = sysUserRoleMapper.listByUserId(userId);
        if (CollectionUtils.isEmpty(sysUserRoles)) {
            return List.of();
        }
        List<Long> roleIds = sysUserRoles.stream().map(SysUserRole::getRoleId).toList();
        return mapper.selectListByIds(roleIds);
    }

    @Override
    public SysRole getByPerm(String member) {
        return mapper.selectByKey(member);
    }
}
