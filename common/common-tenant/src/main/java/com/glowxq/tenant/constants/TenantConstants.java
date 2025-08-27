package com.glowxq.tenant.constants;

/**
 * 租户常量接口 - 定义与多租户功能相关的常量
 * <p>
 * 此接口包含用于标识和处理租户信息的常量，包括：
 * - 用于HTTP请求参数的租户ID字段名
 * - 数据库表中的租户ID列名
 * - HTTP请求头中的租户ID标识
 * </p>
 *
 * @author glowxq
 * @version 1.0
 * @date 2025/6/5
 */
public interface TenantConstants {

    /**
     * 租户ID请求参数名
     * <p>
     * 用于HTTP请求中作为URL参数传递租户ID，例如：
     * {@code /api/resource?tenantId=1001}
     * </p>
     */
    String TENANT_ID_FILED = "tenantId";

    /**
     * 租户ID数据库列名
     * <p>
     * 在数据库表中存储租户ID的标准列名，遵循下划线命名规范。
     * 用于多租户数据隔离，确保查询条件中包含此列作为筛选条件。
     * </p>
     */
    String TENANT_ID_COLUMN = "tenant_id";

    /**
     * 租户ID请求头名称
     * <p>
     * 用于在HTTP请求头中传递租户ID，符合HTTP头部命名规范（短横线分隔）。
     * 优先级高于请求参数中的租户ID。
     * </p>
     * <p>
     * 示例：
     * {@code tenant-id: 1001}
     * </p>
     */
    String TENANT_ID_HEADER = "Tenant-Id";

    /**
     * 根租户ID
     * <p>
     * 根租户ID用于标识系统内置的租户，如超级管理员、系统租户等。
     * 根租户ID通常为常量，用于表示系统内置的租户。
     * </p>
     */
    String ROOT_TENANT_ID = "GLOWXQ";
}
