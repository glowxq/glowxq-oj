package com.glowxq.system.admin.service;

import com.glowxq.core.common.entity.SelectIdsDTO;
import com.glowxq.system.admin.pojo.dto.sysmenu.GenButtonDTO;
import com.glowxq.system.admin.pojo.dto.sysmenu.MenuPermissionDTO;
import com.glowxq.system.admin.pojo.dto.sysmenu.SysMenuCreateDTO;
import com.glowxq.system.admin.pojo.dto.sysmenu.SysMenuListDTO;
import com.glowxq.system.admin.pojo.po.SysMenu;
import com.glowxq.system.admin.pojo.vo.sysmenu.MenuPermissionVO;
import com.glowxq.system.admin.pojo.vo.sysmenu.MenuTreeVO;
import com.glowxq.system.admin.pojo.vo.sysmenu.SysMenuVO;
import com.mybatisflex.core.service.IService;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 系统菜单表 服务类
 * </p>
 *
 * @author glowxq
 * @since 2022-10-01
 */
public interface SysMenuService extends IService<SysMenu> {

    /**
     * 创建
     *
     * @param dto
     *            dto
     */
    void create(SysMenuCreateDTO dto);

    /**
     * 更新
     *
     * @param dto
     *            dto
     */
    void update(SysMenuCreateDTO dto);

    /**
     * 删除菜单
     *
     * @param dto
     *            dto
     */
    void remove(SelectIdsDTO dto);

    /**
     * 列表
     *
     * @param dto
     *            dto
     * @return {@link List}<{@link SysMenuVO}>
     */
    List<SysMenuVO> menuList(SysMenuListDTO dto);

    List<SysMenuVO> findMenuListByUserId(Long userId, String menuType);

    List<MenuTreeVO> getSimpleMenuTree(String nodeId);

    List<MenuTreeVO> getMenuTreeVOS(String nodeId, boolean isShowButton);

    List<MenuTreeVO> queryRoleMenuTree(boolean isShowButton);

    String exportMenuSql(SelectIdsDTO dto);

    /**
     * 详情
     *
     * @return {@link SysMenu}
     */
    SysMenu detail(String id);

    MenuPermissionVO hasExistsPermissions(MenuPermissionDTO dto);

    /**
     * 查询权限按钮
     *
     * @return 权限按钮集合
     */
    List<String> findPermission();

    List<String> findPermissionsByUserId(Long userId);

    List<String> findAllPermissions();

    Map<String, String> getBtnMenuByPermissions(Collection<String> permissions);

    List<MenuTreeVO> queryDataRoleMenu();

    void changeMenuDataScope(String menuId);

    void genButtons(GenButtonDTO dto);
}
