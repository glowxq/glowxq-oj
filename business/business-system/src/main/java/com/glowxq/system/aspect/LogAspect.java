package com.glowxq.system.aspect;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import com.glowxq.core.common.annotation.OperationLog;
import com.glowxq.core.util.JsonUtils;
import com.glowxq.core.util.ServletUtils;
import com.glowxq.security.core.util.LoginUtils;
import com.glowxq.system.admin.pojo.po.SysOperationLog;
import com.glowxq.system.admin.service.SysOperationLogService;
import com.glowxq.tenant.utils.TenantUtils;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 操作日志记录处理
 *
 * @author Lion Li
 */
@Slf4j
@Aspect
@Configuration
public class LogAspect {

    /**
     * 排除敏感属性字段
     */
    public static final String[] EXCLUDE_PROPERTIES = {"password", "oldPassword", "newPassword", "confirmPassword"};

    /**
     * 计算操作消耗时间
     */
    private static final ThreadLocal<StopWatch> TIME_THREADLOCAL = new ThreadLocal<>();

    @Autowired
    private SysOperationLogService operationLogService;

    /**
     * 处理请求前执行
     */
    @Before(value = "@annotation(operationLog)")
    public void boBefore(JoinPoint joinPoint, OperationLog operationLog) {
        StopWatch stopWatch = new StopWatch();
        TIME_THREADLOCAL.set(stopWatch);
        stopWatch.start();
    }

    /**
     * 处理完请求后执行
     *
     * @param joinPoint 切点
     */
    @AfterReturning(pointcut = "@annotation(operationLog)", returning = "jsonResult")
    public void doAfterReturning(JoinPoint joinPoint, OperationLog operationLog, Object jsonResult) {
        handleLog(joinPoint, operationLog, null, jsonResult);
    }

    /**
     * 获取注解中对方法的描述信息 用于Controller层注解
     *
     * @param operationLog     日志
     * @param operLog 操作日志
     * @throws Exception
     */
    public void buildApiData(JoinPoint joinPoint, OperationLog operationLog, SysOperationLog operLog, Object jsonResult) throws Exception {
        // 设置action动作
        operLog.setBusinessType(operationLog.handleType().getCode());
        // 是否需要保存request，参数和值
        if (operationLog.isSaveRequest()) {
            // 获取参数的信息，传入到数据库中。
            setRequestValue(joinPoint, operLog, operationLog.excludeParamNames());
        }
        // 是否需要保存response，参数和值
        if (operationLog.isSaveResponse() && ObjectUtil.isNotNull(jsonResult)) {
            operLog.setResponse(StringUtils.substring(JsonUtils.toJsonString(jsonResult), 0, 2000));
        }
    }

    public String getDescription(JoinPoint joinPoint, OperationLog log) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        OperationLog operationLog = method.getAnnotation(OperationLog.class);
        // 如果没有注解，使用默认值或跳过
        if (operationLog != null && StringUtils.isBlank(operationLog.desc())) {
            return operationLog.desc();
        }

        // 使用swagger里的注解代替操作内容
        Operation operation = method.getAnnotation(Operation.class);
        if (operation != null && StringUtils.isNotBlank(log.desc())) {
            return log.desc();
        }

        return "未记录";
    }

    /**
     * 拦截异常操作
     *
     * @param joinPoint 切点
     * @param e         异常
     */
    @AfterThrowing(value = "@annotation(controllerLog)", throwing = "e")
    public void doAfterThrowing(JoinPoint joinPoint, OperationLog controllerLog, Exception e) {
        handleLog(joinPoint, controllerLog, e, null);
    }

    protected void handleLog(final JoinPoint joinPoint, OperationLog operationLog, final Exception e, Object jsonResult) {
        try {
            // *========数据库日志=========*//
            SysOperationLog operLog = new SysOperationLog();
            operLog.setTenantId(TenantUtils.getTenantId());
            // 请求的地址
            String ip = ServletUtils.getClientIP();
            operLog.setIp(ip);
            operLog.setUserId(LoginUtils.getUserId());
            operLog.setUsername(LoginUtils.getUsername());
            operLog.setUri(StringUtils.substring(ServletUtils.getRequest().getRequestURI(), 0, 255));

            if (e != null) {
                operLog.setErrorMessage(StringUtils.substring(e.getMessage(), 0, 2000));
            }
            // 设置方法名称
            String className = joinPoint.getTarget().getClass().getName();
            String methodName = joinPoint.getSignature().getName();
            operLog.setMethod(className + "." + methodName + "()");
            operLog.setDescription(getDescription(joinPoint, operationLog));
            // 处理设置注解上的参数
            buildApiData(joinPoint, operationLog, operLog, jsonResult);
            // 设置消耗时间
            StopWatch stopWatch = TIME_THREADLOCAL.get();
            stopWatch.stop();
            operLog.setCostTime(stopWatch.getTime(TimeUnit.MILLISECONDS));
            operationLogService.handleLog(operLog);
        } catch (Exception exp) {
            // 记录本地异常日志
            log.error("异常信息:{}", exp.getMessage(), exp);
        } finally {
            TIME_THREADLOCAL.remove();
        }
    }

    /**
     * 获取请求的参数，放到log中
     *
     * @param operLog 操作日志
     * @throws Exception 异常
     */
    private void setRequestValue(JoinPoint joinPoint, SysOperationLog operLog, String[] excludeParamNames) throws Exception {
        HttpServletRequest request = ServletUtils.getRequest();
        Map<String, String> paramsMap = ServletUtils.getParamMap(request);
        MapUtil.removeAny(paramsMap, EXCLUDE_PROPERTIES);
        MapUtil.removeAny(paramsMap, excludeParamNames);
        operLog.setParam(StringUtils.substring(JsonUtils.toJsonString(paramsMap), 0, 2000));

        // TODO 获取请求的参数，放到log中
        // operLog.setRequest(ServletUtils.getRepeatedlyRequestBody(request));
    }
}
