package com.glowxq.system.admin.service.impl;

import com.glowxq.core.common.entity.PageResult;
import com.glowxq.core.common.entity.SelectIdsDTO;
import com.glowxq.core.common.enums.CommonResponseEnum;
import com.glowxq.core.common.event.EventPublisher;
import com.glowxq.core.util.BeanCopyUtils;
import com.glowxq.core.util.PageUtils;
import com.glowxq.core.util.Utils;
import com.glowxq.platform.event.permission.PermissionChangeEvent;
import com.glowxq.platform.event.permission.PermissionMeta;
import com.glowxq.system.admin.mapper.SysDataRoleMapper;
import com.glowxq.system.admin.pojo.dto.sysdatarole.SysDataRoleCreateDTO;
import com.glowxq.system.admin.pojo.dto.sysdatarole.SysDataRoleListDTO;
import com.glowxq.system.admin.pojo.dto.sysdatarole.SysDataRoleUpdateDTO;
import com.glowxq.system.admin.pojo.po.SysDataRole;
import com.glowxq.system.admin.pojo.po.SysUserDataRole;
import com.glowxq.system.admin.pojo.vo.sysdatarole.SysDataRoleMenuVO;
import com.glowxq.system.admin.pojo.vo.sysdatarole.SysDataRoleVO;
import com.glowxq.system.admin.pojo.vo.sysdept.DeptTreeVO;
import com.glowxq.system.admin.pojo.vo.sysmenu.MenuTreeVO;
import com.glowxq.system.admin.pojo.vo.sysuser.UserOptionVO;
import com.glowxq.system.admin.service.*;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.glowxq.system.admin.pojo.po.table.SysUserDataRoleTableDef.SYS_USER_DATA_ROLE;

/**
 * <p>
 * 数据权限管理 服务实现类
 * </p>
 *
 * @author glowxq
 * @since 2024-07-09
 */
@Service
@RequiredArgsConstructor
public class SysDataRoleServiceImpl extends ServiceImpl<SysDataRoleMapper, SysDataRole> implements SysDataRoleService {

    private final SysMenuService sysMenuService;

    private final SysDeptServiceImpl sysDeptService;

    private final SysUserService sysUserService;

    private final SysDataRoleMenuService sysDataRoleMenuService;

    private final SysDataRoleRelationService sysDataRoleRelationService;

    private final EventPublisher eventPublisher;

    @Transactional
    @Override
    public void create(SysDataRoleCreateDTO dto) {
        SysDataRole sysDataRole = BeanCopyUtils.copy(dto, SysDataRole.class);
        save(sysDataRole);
        sysDataRoleMenuService.batchSave(sysDataRole.getId(), dto.getSelectMenuIds());
        if (Utils.isNotNull(dto.getSelectDeptIds()))
            sysDataRoleRelationService.batchSave(sysDataRole.getId(), "1007001", dto.getSelectDeptIds());
        if (Utils.isNotNull(dto.getUserOptions()))
            sysDataRoleRelationService.batchSave(sysDataRole.getId(), "1007002", dto.getUserOptions());

    }

    @Transactional
    @Override
    public void update(SysDataRoleUpdateDTO dto) {
        SysDataRole sysDataRole = BeanCopyUtils.copy(dto, SysDataRole.class);
        QueryWrapper wrapper;
        // id有效性校验
        wrapper = QueryWrapper.create().eq(SysDataRole::getId, dto.getId());
        CommonResponseEnum.INVALID_ID.assertTrue(count(wrapper) <= 0);
        sysDataRoleMenuService.batchSave(sysDataRole.getId(), dto.getSelectMenuIds());
        sysDataRoleRelationService.batchSave(sysDataRole.getId(), "1007001", dto.getSelectDeptIds());
        sysDataRoleRelationService.batchSave(sysDataRole.getId(), "1007002", dto.getUserOptions());
        saveOrUpdate(sysDataRole);

        List<Long> changeUserIds = QueryChain.of(SysUserDataRole.class) // 查询用户影响范围
                .select(SYS_USER_DATA_ROLE.USER_ID).where(SYS_USER_DATA_ROLE.ROLE_ID.eq(dto.getId())).listAs(Long.class);
        eventPublisher.publish(new PermissionChangeEvent(this, new PermissionMeta(changeUserIds)));
    }

    @Override
    public PageResult<SysDataRoleVO> page(SysDataRoleListDTO dto) {
        Page<SysDataRoleVO> page = pageAs(PageUtils.getPage(dto), buildQueryWrapper(dto), SysDataRoleVO.class);
        return PageUtils.getPageResult(page);
    }

    @Override
    public List<SysDataRoleVO> list(SysDataRoleListDTO dto) {
        return listAs(buildQueryWrapper(dto), SysDataRoleVO.class);
    }

    @Override
    public void remove(SelectIdsDTO dto) {
        CommonResponseEnum.INVALID_ID.assertTrue(dto.getIds().isEmpty());
        removeByIds(dto.getIds());
    }

    @Override
    public SysDataRoleVO detail(Object id) {
        Long longVal = Utils.getLongVal(id);
        SysDataRole sysDataRole = getById(longVal);
        CommonResponseEnum.INVALID_ID.assertNull(sysDataRole);
        List<String> menuIds = sysDataRoleMenuService.getSelectMenuIdByRoleId(Utils.getLongVal(id));
        List<Long> deptIds = sysDataRoleRelationService.getSelectRelationId(Utils.getLongVal(id), "1007001");
        List<Long> userOptions = sysDataRoleRelationService.getSelectRelationId(Utils.getLongVal(id), "1007002");
        SysDataRoleVO vo = BeanCopyUtils.copy(sysDataRole, SysDataRoleVO.class);
        vo.setSelectMenuIds(menuIds);
        vo.setSelectDeptIds(deptIds);
        vo.setUserOptions(userOptions);
        return vo;
    }

    private static QueryWrapper buildQueryWrapper(SysDataRoleListDTO dto) {
        QueryWrapper wrapper = QueryWrapper.create().from(SysDataRole.class);
        if (Utils.isNotNull(dto.getRoleName())) {
            wrapper.like(SysDataRole::getRoleName, dto.getRoleName());
        }
        if (Utils.isNotNull(dto.getIsLock())) {
            wrapper.eq(SysDataRole::getIsLock, dto.getIsLock());
        }
        return wrapper;
    }

    @Override
    public SysDataRoleMenuVO queryDataRoleMenu() {
        SysDataRoleMenuVO menuVO = new SysDataRoleMenuVO();
        List<MenuTreeVO> menuTreeVOS = sysMenuService.queryDataRoleMenu();
        List<DeptTreeVO> deptTreeVOS = sysDeptService.getDeptTree(null, false, false);
        List<UserOptionVO> userOptions = sysUserService.getUserOptions();
        menuVO.setMenuLists(menuTreeVOS);
        menuVO.setDeptLists(deptTreeVOS);
        menuVO.setUserOptions(userOptions);
        return menuVO;
    }

}