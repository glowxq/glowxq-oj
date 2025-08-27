package com.glowxq.core.datascope;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * 数据权限AOP切面
 * <p>
 * 处理标注了 {@link DataScopeHandle} 注解的方法，
 * 在方法执行前后设置和清理数据权限相关的ThreadLocal变量。
 * </p>
 *
 * @author glowxq
 * @since 2024/7/12
 */
@Aspect
@Component
@Order(1)
@Slf4j
public class DataScopeAspect {

    /**
     * 定义切点
     */
    @Pointcut("@annotation(com.glowxq.core.datascope.DataScopeHandle)")
    public void dataScopePointCut() {
        // 定义切点，不需要实现
    }

    /**
     * 环绕通知
     *
     * @param point 连接点
     * @return 目标方法执行结果
     * @throws Throwable 执行异常
     */
    @Around("dataScopePointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        try {
            // 获取方法上的数据权限注解
            DataScopeHandle dataScopeHandle = getDataScopeHandle(point);
            if (dataScopeHandle != null) {
                // 设置ThreadLocal变量
                setupDataScope(dataScopeHandle);
            }
            // 执行目标方法
            return point.proceed();
        } finally {
            // 清理ThreadLocal，防止内存泄漏
            DataScopeThreadLocal.clear();
        }
    }

    /**
     * 获取方法上的数据权限注解
     *
     * @param joinPoint 连接点
     * @return 数据权限注解
     */
    private DataScopeHandle getDataScopeHandle(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        return method.getAnnotation(DataScopeHandle.class);
    }

    /**
     * 设置数据权限ThreadLocal变量
     *
     * @param dataScopeHandle 数据权限注解
     */
    private void setupDataScope(DataScopeHandle dataScopeHandle) {
        log.debug("设置数据权限过滤: enabled={}, filterSuperAdmin={}, ignoreTables={}",
                dataScopeHandle.enabled(), dataScopeHandle.filterSuperAdmin(), dataScopeHandle.ignoreTables());
        
        // 设置是否启用数据权限
        DataScopeThreadLocal.setDataScopeEnabled(dataScopeHandle.enabled());
        
        // 设置是否对超级管理员进行过滤
        DataScopeThreadLocal.setSuperAdminFilter(dataScopeHandle.filterSuperAdmin());
        
        // 设置忽略的表
        DataScopeThreadLocal.setIgnoreTables(dataScopeHandle.ignoreTables());
    }
}
