package com.glowxq.oj.common;

import com.glowxq.core.common.entity.LoginUser;
import com.glowxq.core.common.exception.common.AlertsException;
import com.glowxq.security.core.constants.RoleConstant;
import com.glowxq.security.core.util.LoginUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;

import java.util.List;
import java.util.Set;

public class DataScopeUtils {

    /**
     * Determines if the currently logged-in user has a role that includes "admin".
     * This method retrieves the logged-in user via {@link LoginUtils#getLoginUser()} and checks their roles.
     * If the user is not logged in or no roles contain "admin", the method returns false.
     * The check is case-insensitive and looks for the presence of {@link RoleConstant#ADMIN} within any role string.
     *
     * @return true if the logged-in user has a role containing "admin", false otherwise
     */
    public static Boolean scopeEnable() {
        LoginUser loginUser = LoginUtils.getLoginUser();
        if (loginUser == null) {
            return false;
        }
        return !loginUser.containsAdminRole();
    }

    public static List<Long> scope(List<Long> groupIds) {
        LoginUser loginUser = LoginUtils.getLoginUser();
        List<Long> joinGroupIds = DataScopeUtils.scopeGroupIds().stream().toList();
        // 如果用户主动没有主动筛选数据， 非管理员则只返回自己加入的班级，管理员则不筛选
        if (CollectionUtils.isEmpty(groupIds)) {
            if (loginUser.containsAdminRole()) {
                throw new AlertsException("管理员且没有筛选数据，应该在上层进行拦截");
            }
            else {
                return joinGroupIds;
            }
        }
        // 如果用户筛选了数据，非管理员则取交集，管理员则不变，要筛选啥直接筛选即可
        else {
            if (loginUser.containsAdminRole()) {
                return groupIds;
            }
            else {
                groupIds = ListUtils.intersection(groupIds, joinGroupIds);
            }
        }
        return groupIds;
    }

    /**
     * 如果用户一个班级都没加入 返回Long.MIN_VALUE 默认筛选值
     *
     * @return
     */
    public static Set<Long> scopeGroupIds() {
        LoginUser loginUser = LoginUtils.getLoginUser();
        Set<Long> groupIds = loginUser.getGroupIds();
        if (CollectionUtils.isEmpty(groupIds)) {
            return Set.of(Long.MIN_VALUE);
        }
        return groupIds;
    }
}
