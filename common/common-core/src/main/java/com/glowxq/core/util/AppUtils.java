package com.glowxq.core.util;

import com.glowxq.core.common.configuration.AppConfig;
import com.glowxq.core.common.enums.EnvType;

/**
 * 应用程序使用情况
 *
 * @author glowxq
 * @date 2024/03/01
 */
public class AppUtils {

    /**
     * 应用配置
     */
    private static final AppConfig APP_CONFIG = SpringUtils.getBean(AppConfig.class);

    /**
     * 获取版本
     *
     * @return
     */
    public static String getVersion() {
        return APP_CONFIG.getVersion();
    }

    /**
     * 获取应用名称
     *
     * @return
     */
    public static String getAppName() {
        return APP_CONFIG.getName();
    }

    /**
     * 获取业务
     *
     * @return {@link String }
     */
    public static String getBusinessPath() {
        return APP_CONFIG.getBusiness();
    }

    /**
     * 获取业务api前缀
     * @return
     */
    public static String getBusinessApiPrefix() {
        return StringUtils.upperCase(APP_CONFIG.getBusiness());
    }

    /**
     * 非生产环境
     *
     * @return {@link Boolean }
     */
    public static Boolean isNotProd() {
        return !isProd();
    }

    /**
     * 生产环境
     *
     * @return {@link Boolean }
     */
    public static Boolean isProd() {
        EnvType envType = getEnvironment();
        return envType.equals(EnvType.Prod);
    }

    /**
     * 开发环境
     *
     * @return {@link Boolean }
     */
    public static Boolean isDev() {
        EnvType envType = getEnvironment();
        return envType.equals(EnvType.Dev);
    }

    /**
     * 获取环境
     *
     * @return {@link String }
     */
    public static EnvType getEnvironment() {
        return EnvType.matchCode(APP_CONFIG.getEnvironment());
    }

    public static boolean isLocal() {
        return getEnvironment().equals(EnvType.Local);
    }

    public static boolean isLocalOrDev(){
        return isLocal() || isDev();
    }
}
