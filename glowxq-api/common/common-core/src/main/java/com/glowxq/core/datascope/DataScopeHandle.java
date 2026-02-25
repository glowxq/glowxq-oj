package com.glowxq.core.datascope;

import java.lang.annotation.*;

/**
 * 数据权限处理注解
 * <p>
 * 该注解用于标记需要进行数据权限过滤的方法。
 * 通过配置不同的参数，可以灵活控制数据权限过滤的行为。
 * </p>
 *
 * @author glowxq
 * @since 2024/7/12
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataScopeHandle {

    /**
     * 是否开启数据权限过滤
     * <p>
     * 默认为true，表示开启数据权限过滤
     * </p>
     *
     * @return 是否开启数据权限过滤
     */
    boolean enabled() default true;

    /**
     * 超级管理员是否也进行数据权限过滤
     * <p>
     * 默认为false，表示超级管理员不受数据权限限制
     * </p>
     *
     * @return 超级管理员是否也进行数据权限过滤
     */
    boolean filterSuperAdmin() default false;

    /**
     * 需要忽略的表
     * <p>
     * 指定不需要进行数据权限过滤的表名
     * </p>
     *
     * @return 需要忽略的表名数组
     */
    String[] ignoreTables() default {};
}
