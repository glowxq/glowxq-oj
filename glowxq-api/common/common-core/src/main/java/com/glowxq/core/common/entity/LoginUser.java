package com.glowxq.core.common.entity;

import com.glowxq.core.common.enums.DataScopeType;
import com.glowxq.core.util.StringUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;

@Data
public class LoginUser {

    @Schema(description = "基础用户信息")
    private BaseUserInfo userInfo;

    @Schema(description = "权限标识列表")
    private Set<String> permissions = new HashSet<>();

    @Schema(description = "角色列表")
    private Set<String> roles = new HashSet<>();

    @Schema(description = "角色key列表")
    private Set<String> roleKeys = new HashSet<>();

    @Schema(description = "班级id列表")
    private Set<Long> groupIds = new HashSet<>();

    @Schema(description = "数据权限")
    private String dataScope;

    @Schema(description = "所属部门")
    private List<Long> depts = new ArrayList<>();

    @Schema(description = "所属部门及其子孙节点部门")
    private List<Long> deptAndChildren = new ArrayList<>();

    @Schema(description = "权限标识与菜单关系数组")
    private Map<String, String> permissionAndMenuIds = new HashMap<>();

    @Schema(description = "菜单的查询规则")
    private Map<String, String> ruleMap = new HashMap<>();

    @Schema(description = "自定义userRule")
    private Map<String, Set<Long>> userRuleMap = new HashMap<>();

    @Schema(description = "自定义deptRule")
    private Map<String, Set<Long>> deptRuleMap = new HashMap<>();

    public DataScopeType dataScope() {
        return DataScopeType.matchCode(dataScope);
    }

    public Long getUserId() {
        return userInfo.getId();
    }

    public String getTenantId() {
        return userInfo.getTenantId();
    }

    public Boolean containsAdminRole() {
        Set<String> roles = this.getRoleKeys();
        for (String role : roles) {
            // 如果存在包含admin的角色
            if (StringUtils.containsAnyIgnoreCase(role, "admin")) {
                return true;
            }
        }
        return false;
    }

    public Set<Long> scopeGroupIds() {
        if (CollectionUtils.isEmpty(groupIds)) {
            return Set.of(Long.MIN_VALUE);
        }
        return groupIds;
    }

    public Boolean containsRole(String roleKey) {
        return getRoleKeys().contains(roleKey);
    }


    public Long getDeptId() {
        if (CollectionUtils.isEmpty(depts)) {
            return Long.MIN_VALUE;
        }
        return depts.getFirst();
    }
}
