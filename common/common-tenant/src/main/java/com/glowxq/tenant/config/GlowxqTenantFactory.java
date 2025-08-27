package com.glowxq.tenant.config;

import com.glowxq.tenant.utils.TenantUtils;
import com.mybatisflex.core.tenant.TenantFactory;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 租户工厂实现类 - 为MyBatis Flex提供租户ID
 * <p>
 * 实现TenantFactory接口，以便在MyBatis Flex进行数据库操作时
 * 自动添加租户条件，实现多租户数据隔离
 * </p>
 *
 * @author glowxq
 * @version 1.0
 * @date 2025/6/5
 */
@Slf4j
@Data
public class GlowxqTenantFactory implements TenantFactory {

    /**
     * 需要忽略租户过滤的表名列表
     */
    private List<String> ignoreTables = new ArrayList<>();

    /**
     * 是否启用多租户功能
     */
    private Boolean enable = Boolean.TRUE;

    /**
     * 获取当前租户ID数组（兼容旧版本接口）
     * <p>
     * 该方法由MyBatis Flex旧版本调用，用于在SQL执行时注入租户条件
     * 现在将调用重定向到新版本的方法
     * </p>
     *
     * @return 包含当前租户ID的数组，如果没有租户ID则返回null
     */
    @Override
    public Object[] getTenantIds() {
        return getTenantIds(null);
    }

    /**
     * 获取当前租户ID数组
     * <p>
     * 该方法由MyBatis Flex调用，用于在SQL执行时注入租户条件
     * 功能：
     * 1. 检查多租户功能是否启用
     * 2. 检查表是否在忽略列表中
     * 3. 获取并返回当前租户ID
     * </p>
     *
     * @param tableName 当前操作的表名
     * @return 包含当前租户ID的数组，如果没有租户ID或表被忽略则返回null
     */
    @Override
    public Object[] getTenantIds(String tableName) {
        // 检查多租户功能是否启用
        if (!enable) {
            log.debug("多租户功能已禁用，跳过租户过滤");
            return null;
        }

        // 检查表是否在忽略列表中
        if (StringUtils.isNotBlank(tableName) && CollectionUtils.isNotEmpty(this.ignoreTables) && this.ignoreTables.contains(tableName)) {
            log.debug("表[{}]在忽略列表中，跳过租户过滤", tableName);
            return null;
        }

        // 获取当前租户ID
        String tenantId = TenantUtils.getTenantId();
        log.debug("MyBatis Flex租户工厂获取租户ID: {}", tenantId);

        if (StringUtils.isBlank(tenantId)) {
            log.debug("租户ID为空，不应用租户过滤条件");
            return null;
        }

        log.debug("应用租户过滤条件，表名: {}, 租户ID: {}", tableName, tenantId);
        return new Object[]{tenantId};
    }


}