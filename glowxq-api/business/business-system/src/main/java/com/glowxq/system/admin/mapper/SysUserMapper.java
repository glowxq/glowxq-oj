package com.glowxq.system.admin.mapper;

import com.glowxq.system.admin.pojo.dto.sysuser.SysUserListDTO;
import com.glowxq.system.admin.pojo.po.SysUser;
import com.glowxq.system.admin.pojo.vo.sysuser.SysUserVO;
import com.glowxq.system.admin.pojo.vo.sysuser.UserDeptInfoVO;
import com.glowxq.system.admin.pojo.vo.sysuser.UserRoleInfoVO;
import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.query.QueryWrapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 系统用户表 Mapper 接口
 * </p>
 *
 * @author glowxq
 * @since 2023-08-18
 */
public interface SysUserMapper extends BaseMapper<SysUser> {

    @Select(" select sr.role_name from sys_user_role sur left join sys_role sr on sur.role_id = sr.id where sur.user_id =#{userId} ")
    List<String> queryUserRoles(Integer userId);

    /**
     * 验证用户名是否存在
     *
     * @param username
     *            用户名
     * @return 用户数量
     */
    @Select(" select count(id) from sys_user where username = #{username}  ")
    int validUsername(String username);

    /**
     * 查询全部部门的用户列表
     * 
     * @param dto
     *            查询条件
     * @return 用户列表
     */
    List<SysUserVO> queryAllSysUserList(SysUserListDTO dto);

    /**
     * 查询全部部门的用户名称列表
     * 
     * @param ids
     *            用户id
     * @return 用户名称列表
     */
    List<SysUserVO> queryAllSysUserNameList(String[] ids);

    /**
     * 查询（部门）用户列表
     * 
     * @param dto
     *            查询条件
     * @return 用户列表
     */
    List<SysUserVO> querySysUserListByDept(SysUserListDTO dto);

    /**
     * 查询（未分配部门）用户列表
     * 
     * @param dto
     *            查询条件
     * @return 用户列表
     */
    List<SysUserVO> querySysUserListNotDept(SysUserListDTO dto);

    /**
     * 查询用户部门信息
     * 
     * @param userIds
     *            用户id
     * @return 用户部门信息
     */
    List<UserDeptInfoVO> queryUserDeptInfo(List<Long> userIds);

    /**
     * 查询用户部门信息
     * 
     * @param userIds
     *            用户id
     * @return 用户部门信息
     */
    List<UserRoleInfoVO> queryUserRoleInfo(List<Long> userIds);

    /**
     * 查询（未分配部门）用户数量
     */
    Integer countSysUserListNotDept();

    default SysUser getByUsername(String username) {
        QueryWrapper wrapper = QueryWrapper.create();
        wrapper.eq(SysUser::getUsername, username);
        return this.selectOneByQuery(wrapper);
    }

    default SysUser getByOpenid(String openid){
        QueryWrapper wrapper = QueryWrapper.create();
        wrapper.eq(SysUser::getOpenid, openid);
        return this.selectOneByQuery(wrapper);
    }
}
