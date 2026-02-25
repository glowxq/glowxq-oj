package com.glowxq.tenant.utils;

import cn.hutool.core.util.RandomUtil;
import com.glowxq.core.util.SpringUtils;
import com.glowxq.security.core.util.LoginUtils;
import com.glowxq.tenant.config.TenantConfig;
import com.glowxq.tenant.constants.TenantConstants;
import com.glowxq.tenant.context.TenantContext;
import com.mybatisflex.core.tenant.TenantManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.function.Supplier;

/**
 * 租户工具类 - 用于获取和管理租户ID，以及处理租户相关的操作
 * <p>
 * 本工具类提供以下功能：
 * - 获取当前租户ID（从登录用户或上下文中）
 * - 生成唯一的租户ID
 * - 执行忽略租户条件的操作
 * - 判断当前租户是否为根租户
 * </p>
 *
 * @author glowxq
 * @version 1.0
 * @date 2025/6/5
 */
@Slf4j
public class TenantUtils {

    private static final TenantConfig tenantConfig = SpringUtils.getBean(TenantConfig.class);

    public static Boolean isEnable() {
        return tenantConfig.getEnable();
    }

    /**
     * 获取当前租户ID
     * 首先尝试从登录用户中获取，如果未登录或未设置，则从租户上下文中获取
     *
     * @return 当前租户ID，如果未设置则返回null
     */
    public static String getTenantId() {
        String tenantId = LoginUtils.getTenantId();
        String contextTenantId = TenantContext.getTenantId();

        String resultTenantId = StringUtils.defaultIfBlank(tenantId, contextTenantId);
        log.info("获取租户ID: {}, 来源: {}", resultTenantId, tenantId != null ? "用户登录信息" : (contextTenantId != null ? "租户上下文" : "未找到租户信息"));

        return resultTenantId;
    }

    public static void setTenantId(String tenantId) {
        TenantContext.setTenantId(tenantId);
    }

    /**
     * 临时忽略租户条件执行操作
     * <p>
     * 此方法在执行提供的操作期间，暂时禁用租户过滤条件，常用于需要跨租户查询的场景。
     * 无论操作是否成功，都会在操作完成后恢复租户条件。
     * </p>
     *
     * @param supplier 需要在忽略租户条件下执行的操作
     * @param <T>      操作返回的结果类型
     * @return 操作的执行结果
     */
    public static <T> T ignoreTenant(Supplier<T> supplier) {
        try {
            TenantManager.ignoreTenantCondition();
            return supplier.get();
        } finally {
            TenantManager.restoreTenantCondition();
        }
    }

    /**
     * 判断当前租户是否为根租户
     * <p>
     * 根租户通常拥有系统的最高权限，可以管理其他租户。
     * 此方法通过比较当前租户ID与根租户代码来确定。
     * </p>
     *
     * @return 如果当前租户是根租户则返回true，否则返回false
     */
    public static boolean isRootTenant() {
        return TenantConstants.ROOT_TENANT_ID.equals(getTenantId());
    }

    /**
     * 生成唯一的租户ID
     * <p>
     * 生成一个不超过10位的唯一租户ID，由大写字母和数字组成。
     * 生成算法确保了极低的重复率，适用于系统中唯一标识租户的场景。
     * </p>
     *
     * @return 10位的唯一租户ID，由大写字母和数字组成
     */
    public static String generateTenantId() {
        // 获取UUID的前4位作为基础（移除连字符后）
        String prefix = "TEN";

        // 生成4位随机大写字母
        String letters = RandomUtil.randomStringUpper(7);

        return prefix + letters;
    }

    /**
     * 临时忽略租户条件执行操作
     * <p>
     * 此方法在执行提供的操作期间，暂时禁用租户过滤条件，常用于需要跨租户查询的场景。
     * 无论操作是否成功，都会在操作完成后恢复租户条件。
     * </p>
     *
     * @param supplier 需要在忽略租户条件下执行的操作
     * @param <T>      操作返回的结果类型
     * @return 操作的执行结果
     */
    public static <T> T changeTenant(String newTenantId, Supplier<T> supplier) {
        String originalTenantId = getTenantId();
        log.info("originalTenantId:{} newTenantId:{}", originalTenantId, newTenantId);
        try {
            setTenantId(newTenantId);
            log.info("切换newTenantId:{}", newTenantId);
            return supplier.get();
        } finally {
            setTenantId(originalTenantId);
            log.info("切换回 originalTenantId:{}", originalTenantId);
        }
    }

    public static void remove() {
        TenantContext.remove();
    }
}
