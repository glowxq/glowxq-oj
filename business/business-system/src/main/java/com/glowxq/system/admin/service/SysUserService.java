package com.glowxq.system.admin.service;

import com.glowxq.core.common.entity.LoginUser;
import com.glowxq.core.common.entity.PageResult;
import com.glowxq.core.common.entity.SelectIdsDTO;
import com.glowxq.system.admin.pojo.dto.RegisterDTO;
import com.glowxq.system.admin.pojo.dto.SearchUserDTO;
import com.glowxq.system.admin.pojo.dto.sysmenu.SysUserRoleDTO;
import com.glowxq.system.admin.pojo.dto.sysuser.*;
import com.glowxq.system.admin.pojo.po.SysUser;
import com.glowxq.system.admin.pojo.vo.SearchUserVO;
import com.glowxq.system.admin.pojo.vo.sysuser.SysUserRoleVO;
import com.glowxq.system.admin.pojo.vo.sysuser.SysUserVO;
import com.glowxq.system.admin.pojo.vo.sysuser.UserOptionVO;
import com.mybatisflex.core.service.IService;

import java.util.List;

/**
 * <p>
 * 系统用户表 服务类
 * </p>
 *
 * @author glowxq
 * @since 2022-10-01
 */
public interface SysUserService extends IService<SysUser> {

    SysUser getByUsername(String username);

    SysUserVO getSysUserByUserId(Long userId);

    /**
     * 创建用户
     *
     * @param dto
     *            用户信息
     */
    SysUser create(SysUserCreateDTO dto);

    /**
     * 更新用户
     *
     * @param dto
     *            用户信息
     */
    SysUser update(SysUserUpdateDTO dto);

    /**
     * 删除用户
     *
     * @param dto
     *            用户id数组
     */
    void remove(SelectIdsDTO dto);

    /**
     * 详情
     *
     * @param id
     *            id
     * @return {@link SysUser}
     */
    SysUserVO detail(Long id);

    PageResult<SysUserVO> page(SysUserListDTO dto);

    SysUserRoleVO findSysUserRole(Long userId);

    void changeSysUserRole(SysUserRoleDTO dto);

    /**
     * 获取用户信息
     *
     * @return {@link SysUserVO}
     */
    SysUserVO getUserInfo();

    /**
     * 更改密码
     *
     * @param dto
     *            dto
     */
    void changePassword(SysUserPasswordDTO dto);

    /**
     * 重置密码
     *
     * @param id
     *            id
     */
    void resetPassword(Long id);

    void syncUserInfo(Object userId);

    LoginUser buildLoginUser(String username, String password);

    LoginUser buildLoginUser(Long userId);

    void unlock(SelectIdsDTO dto);

    void bindUserDept(UserDeptDTO dto);

    List<UserOptionVO> getUserOptions();

    PageResult<SearchUserVO> searchUserList(SearchUserDTO searchKey);

    SysUser register(RegisterDTO registerParam);

    LoginUser buildLoginUser(String openid);
}
