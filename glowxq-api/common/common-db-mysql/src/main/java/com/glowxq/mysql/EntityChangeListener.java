package com.glowxq.mysql;

import cn.dev33.satoken.stp.StpUtil;
import com.glowxq.core.common.entity.LoginUser;
import com.glowxq.mysql.constants.DbConstants;
import com.glowxq.security.core.util.LoginUtils;
import com.glowxq.tenant.constants.TenantConstants;
import com.glowxq.tenant.utils.TenantUtils;
import com.mybatisflex.annotation.InsertListener;
import com.mybatisflex.annotation.SetListener;
import com.mybatisflex.annotation.UpdateListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.beans.PropertyDescriptor;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 数据表变更事件处理器。
 * <p>
 * 约定：数据表必须包含以下字段：
 * <ul>
 * <li>`create_id` - int 类型，创建者 ID</li>
 * <li>`create_time` - datetime 类型，创建时间</li>
 * <li>`update_id` - int 类型，更新者 ID</li>
 * <li>`update_time` - datetime 类型，更新时间</li>
 * </ul>
 *
 * @version 1.0
 * @since 2023-12-08
 */
@Slf4j
public class EntityChangeListener implements InsertListener, UpdateListener, SetListener {

    /**
     * 实体插入事件处理
     * <p>
     * 在实体被插入数据库前自动填充以下字段：
     * 1. 创建时间和更新时间设置为当前时间
     * 2. 填充租户ID
     * 3. 如果用户已登录，则填充创建者ID和更新者ID
     * 4. 如果用户有部门信息，则填充部门范围
     * </p>
     *
     * @param o 待插入的实体对象
     */
    @Override
    public void onInsert(Object o) {
        // 设置时间字段
        setPropertyIfPresent(o, DbConstants.FIELD_CREATE_TIME, LocalDateTime.now());
        setPropertyIfPresent(o, DbConstants.FIELD_UPDATE_TIME, LocalDateTime.now());

        // 设置租户ID
        setPropertyIfPresent(o, TenantConstants.TENANT_ID_FILED, TenantUtils.getTenantId());

        // 如果未登录，则不设置用户相关字段
        if (LoginUtils.isNotLogin()) {
            return;
        }

        // 设置创建者和更新者ID
        setPropertyIfPresent(o, DbConstants.FIELD_CREATE_ID, StpUtil.getLoginIdAsLong());
        setPropertyIfPresent(o, DbConstants.FIELD_UPDATE_ID, StpUtil.getLoginIdAsLong());

        // 设置部门范围
        LoginUser loginUser = LoginUtils.getLoginUser();
        List<Long> deptOptions = loginUser.getDepts();
        if (deptOptions.isEmpty()) {
            return;
        }
        setPropertyIfPresent(o, DbConstants.FIELD_DEPT_SCOPE, deptOptions);
        setPropertyIfPresent(o, DbConstants.FIELD_DEPT_ID, loginUser.getDeptId());
    }

    /**
     * 实体更新事件处理
     * <p>
     * 在实体被更新前自动填充以下字段：
     * 1. 更新时间设置为当前时间
     * 2. 如果用户已登录，则填充更新者ID
     * </p>
     *
     * @param o 待更新的实体对象
     */
    @Override
    public void onUpdate(Object o) {
        // 设置更新时间
        setPropertyIfPresent(o, DbConstants.FIELD_UPDATE_TIME, LocalDateTime.now());

        // 如果未登录，则不设置更新者ID
        if (LoginUtils.isNotLogin()) {
            return;
        }

        // 设置更新者ID
        setPropertyIfPresent(o, DbConstants.FIELD_UPDATE_ID, StpUtil.getLoginIdAsLong());
    }

    /**
     * 属性设置事件处理
     * <p>
     * 在实体属性被设置时调用，当前实现直接返回原始值，不做处理
     * </p>
     *
     * @param entity 实体对象
     * @param property 属性名
     * @param value 属性值
     * @return 处理后的属性值
     */
    @Override
    public Object onSet(Object entity, String property, Object value) {
        return value;
    }

    /**
     * 设置实体属性值（如果属性存在）
     * <p>
     * 使用Spring BeanWrapper优化反射操作，提高性能并减少异常
     * 先检查属性是否存在，再检查当前值是否为null，最后设置新值
     * </p>
     *
     * @param object 目标实体对象
     * @param propertyName 属性名称
     * @param propertyValue 要设置的属性值
     */
    private void setPropertyIfPresent(Object object, String propertyName, Object propertyValue) {
        if (object == null || propertyName == null || propertyValue == null) {
            return;
        }

        try {
            BeanWrapper beanWrapper = new BeanWrapperImpl(object);

            // 检查属性是否存在且可写
            if (!hasWritableProperty(beanWrapper, propertyName)) {
                log.debug("Property '{}' not found or not writable in class '{}'",
                        propertyName, object.getClass().getSimpleName());
                return;
            }

            // 检查当前属性值是否为null，如果不为null则不覆盖
            Object currentValue = beanWrapper.getPropertyValue(propertyName);
            if (currentValue != null) {
                return;
            }

            // 设置属性值
            beanWrapper.setPropertyValue(propertyName, propertyValue);
        } catch (Exception e) {
            // 记录详细错误信息但不抛出异常，避免影响主业务流程
            log.warn("Failed to set property '{}' with value '{}' on object of type '{}': {}",
                    propertyName, propertyValue, object.getClass().getSimpleName(), e.getMessage());
        }
    }

    /**
     * 检查指定属性是否存在且可写
     * <p>
     * 通过PropertyDescriptor检查属性的存在性和可写性，避免不必要的异常
     * </p>
     *
     * @param beanWrapper  Bean包装器
     * @param propertyName 属性名称
     * @return true表示属性存在且可写，false表示不存在或不可写
     */
    private boolean hasWritableProperty(BeanWrapper beanWrapper, String propertyName) {
        try {
            PropertyDescriptor propertyDescriptor = beanWrapper.getPropertyDescriptor(propertyName);
            return propertyDescriptor != null &&
                    propertyDescriptor.getWriteMethod() != null &&
                    beanWrapper.isWritableProperty(propertyName);
        } catch (Exception e) {
            // 如果获取属性描述符失败，说明属性不存在
            return false;
        }
    }

}
