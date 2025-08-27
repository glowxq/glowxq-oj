package com.glowxq.core.datascope;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 数据权限ThreadLocal工具类
 * <p>
 * 用于在线程上下文中存储和管理数据权限相关的配置信息。
 * </p>
 *
 * @author glowxq
 * @since 2024/7/12
 */
public class DataScopeThreadLocal {

    /**
     * 数据权限启用状态ThreadLocal
     */
    private static final ThreadLocal<Boolean> DATA_SCOPE_ENABLED = new ThreadLocal<>();

    /**
     * 超级管理员过滤状态ThreadLocal
     */
    private static final ThreadLocal<Boolean> SUPER_ADMIN_FILTER = new ThreadLocal<>();

    /**
     * 忽略表ThreadLocal
     */
    private static final ThreadLocal<Set<String>> IGNORE_TABLES = new ThreadLocal<>();

    /**
     * 设置数据权限启用状态
     *
     * @param enabled 是否启用数据权限
     */
    public static void setDataScopeEnabled(boolean enabled) {
        DATA_SCOPE_ENABLED.set(enabled);
    }

    /**
     * 获取数据权限启用状态
     *
     * @return 是否启用数据权限
     */
    public static boolean isDataScopeEnabled() {
        Boolean enabled = DATA_SCOPE_ENABLED.get();
        return enabled != null && enabled;
    }

    /**
     * 设置超级管理员过滤状态
     *
     * @param filter 是否对超级管理员进行数据权限过滤
     */
    public static void setSuperAdminFilter(boolean filter) {
        SUPER_ADMIN_FILTER.set(filter);
    }

    /**
     * 获取超级管理员过滤状态
     *
     * @return 是否对超级管理员进行数据权限过滤
     */
    public static boolean isSuperAdminFilter() {
        Boolean filter = SUPER_ADMIN_FILTER.get();
        return filter != null && filter;
    }

    /**
     * 设置忽略表
     *
     * @param tables 需要忽略的表名数组
     */
    public static void setIgnoreTables(String[] tables) {
        if (tables != null && tables.length > 0) {
            Set<String> tableSet = new HashSet<>(Arrays.asList(tables));
            IGNORE_TABLES.set(tableSet);
        } else {
            IGNORE_TABLES.set(new HashSet<>());
        }
    }

    /**
     * 检查表是否在忽略列表中
     *
     * @param tableName 表名
     * @return 是否忽略该表
     */
    public static boolean isTableIgnored(String tableName) {
        Set<String> tables = IGNORE_TABLES.get();
        return tables != null && tables.contains(tableName);
    }

    /**
     * 清除所有ThreadLocal数据
     */
    public static void clear() {
        DATA_SCOPE_ENABLED.remove();
        SUPER_ADMIN_FILTER.remove();
        IGNORE_TABLES.remove();
    }
}
