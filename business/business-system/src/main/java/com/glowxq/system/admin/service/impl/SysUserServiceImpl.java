package com.glowxq.system.admin.service.impl;

import cn.dev33.satoken.secure.BCrypt;
import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.github.pagehelper.PageHelper;
import com.glowxq.core.common.entity.BaseUserInfo;
import com.glowxq.core.common.entity.LoginUser;
import com.glowxq.core.common.entity.PageResult;
import com.glowxq.core.common.entity.SelectIdsDTO;
import com.glowxq.core.common.enums.CommonResponseEnum;
import com.glowxq.core.common.event.EventPublisher;
import com.glowxq.core.common.exception.common.BusinessException;
import com.glowxq.core.util.*;
import com.glowxq.mysql.DataScopeProperties;
import com.glowxq.platform.event.permission.PermissionChangeEvent;
import com.glowxq.platform.event.permission.PermissionMeta;
import com.glowxq.redis.CommonKeyConstants;
import com.glowxq.redis.RedisCache;
import com.glowxq.redis.RedisUtils;
import com.glowxq.security.core.util.LoginUtils;
import com.glowxq.security.service.AuthService;
import com.glowxq.system.admin.mapper.SysRoleMapper;
import com.glowxq.system.admin.mapper.SysUserMapper;
import com.glowxq.system.admin.mapper.SysUserRoleMapper;
import com.glowxq.system.admin.pojo.dto.RegisterDTO;
import com.glowxq.system.admin.pojo.dto.SearchUserDTO;
import com.glowxq.system.admin.pojo.dto.sysmenu.SysUserRoleDTO;
import com.glowxq.system.admin.pojo.dto.sysuser.*;
import com.glowxq.system.admin.pojo.po.SysRole;
import com.glowxq.system.admin.pojo.po.SysUser;
import com.glowxq.system.admin.pojo.po.SysUserRole;
import com.glowxq.system.admin.pojo.vo.SearchUserVO;
import com.glowxq.system.admin.pojo.vo.sysuser.*;
import com.glowxq.system.admin.service.SysMenuService;
import com.glowxq.system.admin.service.SysPermissionService;
import com.glowxq.system.admin.service.SysUserDeptService;
import com.glowxq.system.admin.service.SysUserService;
import com.glowxq.system.enums.DictUserStatusEnum;
import com.glowxq.system.enums.DictUserTypeEnum;
import com.glowxq.tenant.business.pojo.po.Tenant;
import com.glowxq.tenant.business.service.TenantService;
import com.glowxq.tenant.utils.TenantUtils;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.update.UpdateChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 系统用户表 服务实现类
 * </p>
 *
 * @author glowxq
 * @since 2022-10-01
 */
@Service
@Slf4j
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    @Autowired
    protected SysRoleMapper sysRoleMapper;

    @Autowired
    protected SysUserRoleMapper sysUserRoleMapper;

    @Autowired
    protected RedisCache redisCache;

    @Autowired
    protected SysPermissionService sysPermissionService;

    @Autowired
    protected EventPublisher eventPublisher;

    @Autowired
    protected SysUserDeptService userDeptService;

    @Autowired
    protected DataScopeProperties dataScopeProperties;

    @Autowired
    protected SysMenuService menuService;

    @Autowired
    protected AuthService authService;

    @Autowired
    protected TenantService tenantService;

    @Value("${spring.profiles.active}")
    protected String activeProfile;

    /**
     * 获取认证账户信息接角色信息
     *
     * @param username 用户名
     * @return 用户信息
     */
    @Override
    public SysUser getByUsername(String username) {
        SysUser one = mapper.getByUsername(username);
        CommonResponseEnum.BAD_USERNAME_OR_PASSWORD.assertNull(one);
        return one;
    }

    /**
     * 获取认证账户信息接角色信息
     *
     * @param userId 用户id
     * @return 用户信息
     */
    @Override
    public SysUserVO getSysUserByUserId(Long userId) {
        QueryWrapper wrapper = QueryWrapper.create().eq(SysUser::getId, userId);
        SysUser one = getOne(wrapper);
        CommonResponseEnum.BAD_USERNAME_OR_PASSWORD.assertNull(one);
        return BeanCopyUtils.copy(one, SysUserVO.class);
    }

    /**
     * 后台创建用户
     *
     * @param dto 用户信息
     */
    @Transactional
    @Override
    public SysUser create(SysUserCreateDTO dto) {
        String tenantId = StringUtils.defaultIfBlank(dto.getTenantId(), TenantUtils.getTenantId());
        Tenant tenant = tenantService.checkTenantThrow(tenantId);

        SysUser user = BeanCopyUtils.copy(dto, SysUser.class);
        QueryWrapper wrapper = QueryWrapper.create().eq(SysUser::getUsername, dto.getUsername());
        CommonResponseEnum.USERNAME_EXISTS.assertTrue(count(wrapper) > 0);
        String encodePwd = getEncoderPwd(getInitPassword());
        user.setPwd(encodePwd);
        user.setAccountStatusCd(DictUserStatusEnum.Normal.getCode());
        user.setUserTagCd(DictUserTypeEnum.Normal.getCode());
        this.save(user);

        if (dto.getDeptId() != null && dto.getDeptId() >= 0) {
            UserDeptDTO deptDTO = new UserDeptDTO();
            deptDTO.setDeptIds(Collections.singletonList(dto.getDeptId()));
            deptDTO.setUserIds(Collections.singletonList(user.getId()));
            bindUserDept(deptDTO);
        }
        if (TenantUtils.isEnable()) {
            tenant.plusCurrentUserNum();
            tenantService.updateById(tenant);
        }
        return user;
    }

    /**
     * 更新用户
     *
     * @param dto 用户信息
     */
    @Override
    public SysUser update(SysUserUpdateDTO dto) {
        SysUser user = BeanCopyUtils.copy(dto, SysUser.class);
        // 检查用户是否存在
        SysUser one = mapper.selectOneById(dto.getId());
        CommonResponseEnum.INVALID_USER.assertNull(one);
        updateById(user);
        eventPublisher.publish(new PermissionChangeEvent(this, new PermissionMeta(Collections.singletonList(user.getId()))));
        return user;
    }

    /**
     * 删除用户 (逻辑删除，保留数据关系。如部门、权限、角色等)
     *
     * @param dto 用户id数组
     */
    @Override
    @Transactional
    public void remove(SelectIdsDTO dto) {
        QueryWrapper wrapper = QueryWrapper.create().in(SysUser::getId, dto.getIds());
        // 检查用户是否存在
        CommonResponseEnum.INVALID_ID.assertTrue(count(wrapper) < 1);
        removeByIds(dto.getIds());
    }

    /**
     * 详情
     *
     * @param id 用户id
     * @return {@link SysUser}
     */
    @Override
    public SysUserVO detail(Long id) {
        SysUser user = getById(id);
        CommonResponseEnum.INVALID_ID.assertNull(user);
        SysUserVO sysUserVO = new SysUserVO();
        BeanCopyUtils.copy(user, sysUserVO);
        return sysUserVO;
    }

    @Override
    public PageResult<SysUserVO> page(SysUserListDTO dto) {
        PageResult<SysUserVO> result;
        List<SysUserVO> sysUserVOS;
        QueryWrapper wrapper = QueryWrapper.create().from(SysUser.class);
        wrapper.like(SysUser::getNickname, dto.getNickname(), StringUtils.isNotBlank(dto.getNickname()));
        wrapper.like(SysUser::getUsername, dto.getUsername(), StringUtils.isNotBlank(dto.getUsername()));
        wrapper.like(SysUser::getPhone, dto.getPhone(), StringUtils.isNotBlank(dto.getPhone()));
        wrapper.like(SysUser::getName, dto.getName(), StringUtils.isNotBlank(dto.getName()));
        wrapper.orderBy(SysUser::getId).asc();
        if (dto.getDeptId() != null) {
            try {
                PageUtils.toPage(dto);
                if (dto.getDeptId() != null && dto.getDeptId() == -1) { // 查询全部
                    sysUserVOS = this.mapper.queryAllSysUserList(dto);
                }
                else if (dto.getDeptId() != null && dto.getDeptId() == -2) { // 查询未分配部门的列表
                    sysUserVOS = this.mapper.querySysUserListNotDept(dto);
                }
                else { // 查询指定部门的列表
                    sysUserVOS = this.mapper.querySysUserListByDept(dto);
                }
            } finally {
                PageHelper.clearPage();
            }
        }
        else {
            sysUserVOS = mapper.selectListByQueryAs(wrapper, SysUserVO.class);
        }

        setUserDeptInfo(sysUserVOS);
        setUserRoleInfo(sysUserVOS);
        result = PageUtils.getPageResult(sysUserVOS);

        return result;
    }

    @Override
    public SysUserRoleVO findSysUserRole(Long userId) {
        List<SysRole> sysRoleList = QueryChain.of(this.sysRoleMapper).list();
        List<SysUserRoleVO.RoleInfoVO> roleInfoVOS = BeanCopyUtils.copyList(sysRoleList, SysUserRoleVO.RoleInfoVO.class);
        List<SysUserRole> userRoles = QueryChain.of(sysUserRoleMapper).eq(SysUserRole::getUserId, userId).list();
        List<Long> roleIds = new ArrayList<>();
        if (Utils.isNotNull(userRoles)) {
            roleIds = userRoles.stream().map(SysUserRole::getRoleId).collect(Collectors.toList());
        }
        SysUserRoleVO sysUserRoleVO = new SysUserRoleVO();
        sysUserRoleVO.setRoleInfoVOS(roleInfoVOS);
        sysUserRoleVO.setSelectIds(roleIds);
        return sysUserRoleVO;
    }

    @Override
    public void changeSysUserRole(SysUserRoleDTO dto) {
        // 删除当前用户下的所有角色
        UpdateChain.of(sysUserRoleMapper).eq(SysUserRole::getUserId, dto.getUserId()).remove();

        if (Utils.isNotNull(dto.getRoleIds())) {
            sysUserRoleMapper.insertBatchSysUserRole(dto.getRoleIds(), dto.getUserId());
        }

        List<Long> userIds = new ArrayList<>();
        userIds.add(dto.getUserId());
        eventPublisher.publish(new PermissionChangeEvent(this, new PermissionMeta(userIds)));
    }

    /**
     * 获取用户信息
     *
     * @return {@link SysUserVO}
     */
    @Override
    public SysUserVO getUserInfo() {
        SysUser sysUser = getById(Objects.requireNonNull(LoginUtils.getLoginUser()).getUserInfo().getId());
        return BeanCopyUtils.copy(sysUser, SysUserVO.class);
    }

    /**
     * 更改（当前用户）密码
     *
     * @param dto dto
     */
    @Override
    public void changePassword(SysUserPasswordDTO dto) {
        SysUser sysUser = getById(StpUtil.getLoginIdAsLong()); // 获取当前用户id
        CommonResponseEnum.BAD_USERNAME_OR_PASSWORD.assertFalse(matchEncoderPwd(dto.getOldPwd(), sysUser.getPwd()));
        sysUser.setPwd(getEncoderPwd(dto.getNewPwd()));
        updateById(sysUser);
        redisCache.clearUserInfo(sysUser.getUsername());
        authService.kickOut(StpUtil.getLoginIdAsLong());
    }

    /**
     * 重置密码
     *
     * @param id id
     */
    @Override
    public void resetPassword(Long id) {
        SysUser user = getById(id);
        CommonResponseEnum.INVALID_ID.assertNull(user);
        user.setPwd(getEncoderPwd(getInitPassword()));
        updateById(user);
        authService.kickOut(id);
    }

    @Override
    public void syncUserInfo(Object userId) {
        // TODO 同步用户信息 非常耗时，需要优化
        List<String> tokens = StpUtil.getTokenValueListByLoginId(userId);
        if (tokens.isEmpty()) {
            return;
        }

        // TODO 异步处理 暂时使用虚拟线程优化
        // 1. 查询当前用户的最新用户权限信息
        LoginUser loginUser = buildLoginUser(Long.valueOf(userId + ""));
        Thread.ofVirtual().start(() -> {
            for (String token : tokens) {
                // 根据token获取用户session
                SaSession saSession = StpUtil.getTokenSessionByToken(token);
                // 2. 更新redis信息
                saSession.set(LoginUtils.USER_KEY, loginUser);
                log.warn(" 用户元数据变更, 同步更新用户信息, userId:{}, token:{}", userId, token);
            }
        });
    }

    @Override
    public LoginUser buildLoginUser(String username, String password) {
        // boolean hasKey = RedisUtils.hasKey(CommonKeyConstants.SYS_PWD_ERR_CNT, username);
        // Object value = RedisUtils.getValue(CommonKeyConstants.SYS_PWD_ERR_CNT, username);
        // long count = hasKey ? Long.parseLong(String.valueOf(value)) : 0;
        // if (!"preview".equals(activeProfile)) { // 预览环境不做账号锁定
        //     String maxErrCnt = SysConfigUtils.getConfValue("sys.pwd.errCnt");
        //     CommonResponseEnum.CNT_PASSWORD_ERR.assertTrue(hasKey && (count >= Utils.getIntVal(maxErrCnt)));
        // }
        SysUser user = getByUsername(username);
        // 用户状态校验（禁用状态校验）
        validateUserStatus(user);
        // 密码校验
        validatePassword(password, user.getPwd(), username);
        return getLoginUser(user);
    }

    @Override
    public LoginUser buildLoginUser(Long userId) {
        SysUser user = mapper.selectOneById(userId);
        return getLoginUser(user);
    }

    @Override
    public void unlock(SelectIdsDTO dto) {
        if (dto.getIds() == null || dto.getIds().isEmpty()) {
            return;
        }
        String[] ids = dto.getIds().stream().map(Utils::getStringVal).filter(Objects::nonNull).toArray(String[]::new);
        List<SysUserVO> sysUserVOS = this.mapper.queryAllSysUserNameList(ids);
        for (SysUserVO sysUserVO : sysUserVOS) {
            RedisUtils.removeKey(CommonKeyConstants.SYS_PWD_ERR_CNT, Utils.getStringVal(sysUserVO.getUsername()));
        }
    }

    @Override
    public void bindUserDept(UserDeptDTO dto) {
        userDeptService.bind(dto);
        if (Utils.isNotNull(dto.getUserIds())) {
            eventPublisher.publish(new PermissionChangeEvent(this, new PermissionMeta(dto.getUserIds())));
        }
    }

    @Override
    public List<UserOptionVO> getUserOptions() {
        QueryWrapper wrapper = QueryWrapper.create();
        return listAs(wrapper, UserOptionVO.class);
    }

    @Override
    public PageResult<SearchUserVO> searchUserList(SearchUserDTO dto) {
        QueryWrapper wrapper = QueryWrapper.create();
        if (StringUtils.isNotBlank(dto.getSearchKey())) {
            wrapper.or(qw -> {qw.like(SysUser::getUsername, dto);});
            wrapper.or(qw -> {qw.like(SysUser::getNickname, dto);});
            wrapper.or(qw -> {qw.like(SysUser::getName, dto);});
            wrapper.or(qw -> {qw.like(SysUser::getPhone, dto);});
        }
        Page<SearchUserVO> page = mapper.paginateAs(PageUtils.getPage(dto), wrapper, SearchUserVO.class);
        return PageUtils.getPageResult(page);
    }

    @Override
    public SysUser register(RegisterDTO registerParam) {
        Tenant tenant = tenantService.checkTenantThrow(registerParam.getTenantKey());
        SysUser user = TenantUtils.ignoreTenant(() -> mapper.getByUsername(registerParam.getUsername()));
        if (user != null) {
            throw new BusinessException("用户名已存在");
        }
        user = registerParam.buildEntity();
        user.setPwd(getEncoderPwd(registerParam.getPassword()));
        user.setAccountStatusCd(DictUserStatusEnum.Normal.getCode());
        user.setTenantId(tenant.getTenantId());
        user.setUserTagCd(DictUserTypeEnum.Normal.getCode());
        mapper.insert(user, true);

        return user;
    }

    @Override
    public LoginUser buildLoginUser(String openid) {
        SysUser user = mapper.getByOpenid(openid);
        return getLoginUser(user);
    }

    protected void setUserDeptInfo(List<SysUserVO> userList) {
        if (userList.isEmpty()) {
            return;
        }
        // 获取所有用户的 ID 列表
        List<Long> userIds = userList.stream().map(SysUserVO::getId).collect(Collectors.toList());

        // 查询用户的部门信息并转换为 Map
        Map<Long, UserDeptInfoVO> userDeptMap = new HashMap<>();
        List<UserDeptInfoVO> userDeptList = this.mapper.queryUserDeptInfo(userIds);
        if (userDeptList != null) {
            for (UserDeptInfoVO userDeptInfoVO : userDeptList) {
                userDeptMap.put(userDeptInfoVO.getUserId(), userDeptInfoVO);
            }
        }

        // 遍历用户列表，设置用户的部门信息
        for (SysUserVO user : userList) {
            // 检查部门信息是否存在
            if (userDeptMap.containsKey(user.getId())) {
                UserDeptInfoVO infoVO = userDeptMap.get(user.getId());
                user.setDeptInfo(infoVO.getDeptInfos());
                user.setDeptIds(infoVO.getDeptIds());
            }
        }
    }

    protected void setUserRoleInfo(List<SysUserVO> userList) {
        if (userList.isEmpty()) {
            return;
        }
        // 获取所有用户的 ID 列表
        List<Long> userIds = userList.stream().map(SysUserVO::getId).collect(Collectors.toList());

        // 查询用户的部门信息并转换为 Map
        Map<Long, UserRoleInfoVO> userRoleMap = new HashMap<>();
        List<UserRoleInfoVO> userDeptList = this.mapper.queryUserRoleInfo(userIds);
        if (userDeptList != null) {
            for (UserRoleInfoVO infoVO : userDeptList) {
                userRoleMap.put(infoVO.getUserId(), infoVO);
            }
        }
        // 遍历用户列表，设置用户的部门信息
        for (SysUserVO user : userList) {
            // 检查部门信息是否存在
            if (userRoleMap.containsKey(user.getId())) {
                UserRoleInfoVO infoVO = userRoleMap.get(user.getId());
                user.setRoleInfo(infoVO.getRoleInfos());
                user.setRoleIds(infoVO.getRoleIds());
            }
        }
    }

    private String getEncoderPwd(String pwd) {
        return BCrypt.hashpw(pwd, BCrypt.gensalt(10));
    }

    private boolean matchEncoderPwd(String pwd, String pwdEncoder) {
        return BCrypt.checkpw(pwd, pwdEncoder);
    }

    private String getInitPassword() {
        return SysConfigUtils.getConfValue("sys.user.initPwd");
    }

    private LoginUser getLoginUser(SysUser sysUser) {
        CommonResponseEnum.INVALID_USER.assertNull(sysUser);
        BaseUserInfo userInfo = BeanCopyUtils.copy(sysUser, BaseUserInfo.class);
        LoginUser loginUser = new LoginUser();
        loginUser.setUserInfo(userInfo);
        loginUser.setPermissions(sysPermissionService.getMenuPermissions(sysUser)); // 获取用户permissions
        loginUser.setRoles(sysPermissionService.getRoles(sysUser)); // 获取用户角色
        loginUser.setRoleKeys(sysPermissionService.getRoleKeys(sysUser)); // 获取用户角色key
        loginUser.setDepts(sysPermissionService.getDepts(sysUser)); // 获取用户部门
        loginUser.setDeptAndChildren(sysPermissionService.getDeptAndChildren(sysUser)); // 获取用户部门及子孙节点
        if (!dataScopeProperties.isEnabled()) {
            return loginUser; // 未开启数据权限控制，结束逻辑return ！！！
        }

        Map<String, String> btmPermissionMap = menuService.getBtnMenuByPermissions(loginUser.getPermissions());
        Set<String> findMenuIds = new HashSet<>(btmPermissionMap.values());
        loginUser.setPermissionAndMenuIds(btmPermissionMap);
        Map<String, String> ruleMap = sysPermissionService.buildMenuRuleMap(sysUser, findMenuIds);
        String customUserKey = "userRule";
        if (ruleMap.containsKey(customUserKey)) {
            String str = ruleMap.get(customUserKey);
            Map<String, Set<Long>> map = JsonUtils.parseObject(str, new TypeReference<Map<String, Set<Long>>>() {});
            ruleMap.remove(customUserKey);
            loginUser.setUserRuleMap(map);
        }
        String customDeptKey = "deptRule";
        if (ruleMap.containsKey(customDeptKey)) {
            String str = ruleMap.get(customDeptKey);
            Map<String, Set<Long>> map = JsonUtils.parseObject(str, new TypeReference<Map<String, Set<Long>>>() {});
            ruleMap.remove(customDeptKey);
            loginUser.setDeptRuleMap(map);
        }
        loginUser.setRuleMap(ruleMap); // 获取菜单的查询规则
        return loginUser;
    }

    private void validateUserStatus(SysUser user) {
        CommonResponseEnum.BAD_USERNAME_STATUS_INVALID.assertFalse((DictUserStatusEnum.Normal.getCode()).equals(user.getAccountStatusCd()));
    }

    private void validatePassword(String password, String hashedPassword, String username) {
        String timeout = SysConfigUtils.getConfValue("sys_pwd.lockTime");
        boolean checkpwd = BCrypt.checkpw(password, hashedPassword);
        if (!checkpwd) {
            redisCache.countPwdErr(username, Utils.getLongVal(timeout));
        }
        CommonResponseEnum.BAD_USERNAME_OR_PASSWORD.assertFalse(checkpwd);
    }
}