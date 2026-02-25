package com.glowxq.core.common.filter.trace;

import com.glowxq.core.util.JsonUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * TraceLog注解的AOP实现
 * 负责在方法执行前后自动生成和记录spanId，以及记录日志
 *
 * @author glowxq
 * @version 1.0
 * @date 2025/5/26
 */
@Aspect
@Component
public class TraceLogAspect {

    private static final Logger log = LoggerFactory.getLogger(TraceLogAspect.class);

    /**
     * 匹配所有使用了TraceLog注解的方法
     */
    @Pointcut("@annotation(com.glowxq.core.common.filter.trace.TraceLog)")
    public void traceLogPointcut() {
    }

    /**
     * 环绕通知，在方法执行前后添加spanId并记录日志
     *
     * @param joinPoint 切点
     * @return 方法执行结果
     *
     * @throws Throwable 方法执行异常
     */
    @Around("traceLogPointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        // 获取方法签名
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();

        // 获取TraceLog注解
        TraceLog traceLog = method.getAnnotation(TraceLog.class);
        if (traceLog == null) {
            return joinPoint.proceed();
        }

        // 获取方法相关信息
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = method.getName();

        // 获取或生成spanId
        String spanId = TraceLogContext.getSpanId();
        TraceLogContext.setPrefixKey(traceLog.logPrefix());

        // 构建日志前缀
        String logAspect = "%s.%s".formatted(className, methodName);

        // 计时器
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        // 记录方法开始执行日志
        log.info("spanId:{} 开始执行方法: {}", spanId, logAspect);

        // 记录方法参数
        if (traceLog.logParams()) {
            Object[] args = joinPoint.getArgs();
            if (ArrayUtils.isNotEmpty(args)) {
                log.info("spanId:{}  {} 方法参数: {}", spanId, logAspect, JsonUtils.toJsonString(args));
            }
        }

        Object result = null;
        try {
            // 执行方法
            result = joinPoint.proceed();

            // 记录方法返回值
            if (traceLog.logResult() && result != null) {
                log.info("spanId:{} {} 方法返回值: {}", spanId, logAspect, JsonUtils.toJsonString(result));
            }

            return result;
        } catch (Throwable e) {
            // 记录异常信息
            if (traceLog.logException()) {
                log.error("spanId:{} {} 方法执行异常: {}", spanId, logAspect, e.getMessage(), e);
            }
            throw e;
        } finally {
            // 记录方法执行时间
            if (traceLog.logExecutionTime()) {
                stopWatch.stop();
                long timeElapsed = stopWatch.getTime(TimeUnit.MILLISECONDS);
                log.info("spanId:{} {} 方法执行完成，耗时: {}ms", spanId, logAspect, timeElapsed);
            }
            else {
                log.info("spanId:{} {} 方法执行完成", spanId, logAspect);
            }

            // 自动清除上下文中的spanId
            if (traceLog.autoCleanup()) {
                TraceLogContext.clear();
            }
        }
    }
}
