package com.glowxq.oj.user.handler;

import com.glowxq.core.common.enums.DeleteFlag;
import com.glowxq.oj.user.pojo.po.UserInfo;
import com.glowxq.oj.user.service.UserInfoService;
import com.glowxq.security.core.constants.RoleConstant;
import com.glowxq.system.admin.pojo.po.SysUser;
import com.glowxq.system.tenant.BaseTenantBusinessHandler;
import com.glowxq.tenant.business.pojo.po.Tenant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author glowxq
 * @version 1.0
 * @date 2025/6/6
 */
@Slf4j
@Component
@Primary
public class OjTenantBusinessHandlerImpl extends BaseTenantBusinessHandler {

    @Autowired
    UserInfoService userInfoService;

    @Override
    protected void doTenantBusiness(Tenant tenant) {
        SysUser tenantUser = this.createTenantUser(tenant);
        this.addTenantUserRole(tenantUser, RoleConstant.BUSINESS_ADMIN);
        UserInfo userInfo = new UserInfo();
        userInfo.setId(tenantUser.getId());
        userInfo.setUserId(tenantUser.getId());
        userInfo.setDeptId(-1L);
        userInfo.setName(tenant.getContactName());
        userInfo.setNickName(tenant.getSystemName());
        userInfo.setPhone(tenant.getContactPhone());
        userInfo.setEmail(tenant.getContactEmail());
        userInfo.setAvatar(tenant.getLogoUrl());
        userInfo.setSex("");
        userInfo.setBirthday(LocalDate.now());
        userInfo.setImage(tenant.getHomeImageUrl());
        userInfo.setAcNum(0);
        userInfo.setIntegral(0);
        userInfo.setContinuousSignDay(0);
        userInfo.setSubmitNum(0);
        userInfo.setTitle("租户管理员");
        userInfo.setColor("");
        userInfo.setRemark("");
        userInfo.setExpirationTime(LocalDate.now());
        userInfo.setLastLoginTime(LocalDateTime.now());
        userInfo.setLastLoginIp("");
        userInfo.setCreateTime(LocalDateTime.now());
        userInfo.setUpdateTime(LocalDateTime.now());
        userInfo.setDelFlag(DeleteFlag.F.getCode());
        userInfo.setCreateId(0L);
        userInfo.setUpdateId(0L);
        userInfo.setTenantId(tenant.getTenantId());
        userInfoService.save(userInfo);
    }
}
