package com.glowxq.security.core.util;

import cn.dev33.satoken.exception.NotWebContextException;
import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.SaLoginModel;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.ObjectUtil;
import com.glowxq.core.common.constant.GlobalConstant;
import com.glowxq.core.common.entity.BaseUserInfo;
import com.glowxq.core.common.entity.LoginUser;
import com.glowxq.core.common.exception.common.AlertsException;
import com.glowxq.core.util.StringUtils;
import com.glowxq.security.core.constants.RoleConstant;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Optional;

/**
 * @author glowxq
 * @version 1.0
 * @since 2024/1/24 9:38
 */
@Slf4j
public class LoginUtils {

    public static final String USER_KEY = "loginUser";

    private LoginUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static void performLogin(LoginUser loginUser, SaLoginModel model, Map<String, Object> extraData) {
        model = ObjectUtil.defaultIfNull(model, new SaLoginModel());
        model.setExtraData(extraData);
        // 登录，生成token
        StpUtil.login(loginUser.getUserInfo().getId(), model);
        StpUtil.getTokenSession().set(USER_KEY, loginUser);
        // 使用全局配置而不使用model独立配置时间的问题
        StpUtil.getTokenSession().updateTimeout(model.getTimeout());
        StpUtil.getSession().updateTimeout(model.getTimeout());
    }

    public static void performAppletLogin(Object userId, Object loginUser, SaLoginModel model, Map<String, Object> extraData) {
        model = ObjectUtil.defaultIfNull(model, new SaLoginModel());
        model.setExtraData(extraData);
        // 登录，生成token
        StpUtil.login(userId, model);
        StpUtil.getTokenSession().set(USER_KEY, loginUser);
        // 使用全局配置而不使用model独立配置时间的问题
        StpUtil.getTokenSession().updateTimeout(model.getTimeout());
        StpUtil.getSession().updateTimeout(model.getTimeout());
    }

    /**
     * 获取用户
     */
    public static LoginUser getLoginUser() {
        SaSession session = StpUtil.getTokenSession();
        if (ObjectUtil.isNull(session)) {
            throw new AlertsException("用户没有登陆无法获取到session");
        }
        return (LoginUser) session.get(USER_KEY);
    }

    /**
     * 获取登录的tenantId
     */
    public static String getTenantId() {
        if (isNotLogin()) {
            return null;
        }
        SaSession session = StpUtil.getTokenSession();
        if (ObjectUtil.isNull(session)) {
            return null;
        }
        LoginUser loginUser = (LoginUser) session.get(USER_KEY);
        return loginUser.getTenantId();
    }

    /**
     * 检查当前用户是否未登录
     * <p>
     * 安全地检查用户登录状态，处理以下情况：
     * 1. 非Web环境异常（如在后台任务中）
     * 2. 其他可能的异常情况
     * </p>
     *
     * @return true表示用户未登录，false表示用户已登录
     */
    public static boolean isNotLogin() {
        try {
            return !StpUtil.isLogin();
        } catch (NotWebContextException e) {
            // 处理非 Web 环境异常，返回未登录
            log.error("处理非 Web 环境异常，返回未登录 error:{}", e.getMessage());
            return true;
        } catch (Exception e) {
            // 记录所有其他异常，并返回未登录
            log.error("Unexpected error during login check: {}", e.getMessage());
            return true;
        }
    }

    /**
     * 获取用户id
     *
     * @return 用户id
     */
    public static Long getUserId() {
        return StpUtil.getLoginId(-1L);
    }

    /**
     * 根据token获取用户信息
     *
     * @param token token
     * @return 用户信息
     */
    public static LoginUser getLoginUser(String token) {
        SaSession session = StpUtil.getTokenSessionByToken(token);
        if (ObjectUtil.isNull(session)) {
            return null;
        }
        return (LoginUser) session.get(USER_KEY);
    }

    /**
     * 是否是超级管理员
     *
     * @return 是否是超级管理员
     */
    public static boolean isSuperAdmin() {
        if (!StpUtil.isLogin()) {
            return false;
        }
        LoginUser loginUser = getLoginUser();
        if (loginUser == null) {
            return false;
        }
        return loginUser.getRoles().contains(GlobalConstant.SUPER_ROLE);
    }

    public static Boolean hasAnyPermissions(String... permissions) {
        if (!StpUtil.isLogin()) {
            return false;
        }
        for (String permission : permissions) {
            if (Optional.ofNullable(getLoginUser()).map(LoginUser::getPermissions).map(permissionList -> permissionList.contains(permission)).orElse(false)) {
                return true;
            }
        }
        return false;
    }

    public static Boolean hasAnyRoles(String... roleKeys) {
        if (!StpUtil.isLogin()) {
            return false;
        }
        for (String roleKey : roleKeys) {
            LoginUser loginUser = getLoginUser();
            if (Optional.ofNullable(loginUser).map(LoginUser::getRoles).map(roles -> roles.contains(roleKey)).orElse(false)) {
                return true;
            }
        }
        return false;
    }

    public static Boolean isAdmin() {
        return hasAnyRoles(RoleConstant.ROOT, RoleConstant.ADMIN);
    }

    public static boolean isLogin() {
        return StpUtil.isLogin();
    }

    public static String getUsername() {
        return Optional.ofNullable(getLoginUser()).map(LoginUser::getUserInfo).map(BaseUserInfo::getUsername).orElse(StringUtils.EMPTY);
    }

    public static BaseUserInfo getUserInfo() {
        return Optional.ofNullable(getLoginUser()).map(LoginUser::getUserInfo).orElse(new BaseUserInfo());
    }
}
