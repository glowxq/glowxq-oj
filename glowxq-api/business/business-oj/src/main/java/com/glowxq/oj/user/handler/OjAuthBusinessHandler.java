package com.glowxq.oj.user.handler;

import com.glowxq.core.common.entity.LoginUser;
import com.glowxq.core.common.exception.common.BusinessException;
import com.glowxq.oj.group.pojo.po.Group;
import com.glowxq.oj.group.service.GroupService;
import com.glowxq.oj.user.pojo.po.UserInfo;
import com.glowxq.oj.user.service.UserInfoService;
import com.glowxq.system.base.AuthBusinessHandler;
import com.glowxq.system.meta.enums.SystemTagBind;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author glowxq
 * @version 1.0
 * @date 2025/6/23
 */
@Primary
@Component
@RequiredArgsConstructor
public class OjAuthBusinessHandler implements AuthBusinessHandler {

    private final UserInfoService userInfoService;

    private final GroupService groupService;

    @Override
    public void buildLoginUser(LoginUser loginUser) {
        List<Group> groups = groupService.listByBusinessId(loginUser.getUserId(), SystemTagBind.User);
        Set<Long> groupIds = groups.stream().map(Group::getId).collect(Collectors.toSet());
        loginUser.setGroupIds(groupIds);
    }

    @Override
    public void afterCheck(Long userId) {
        UserInfo userInfo = userInfoService.getById(userId);
        LocalDate expirationTime = userInfo.getExpirationTime();
        if (expirationTime != null && expirationTime.isBefore(LocalDate.now())) {
            throw new BusinessException("账号已过期");
        }
    }

    @Override
    public void loginAfter(Long userId) {
        UserInfo userInfo = userInfoService.getById(userId);
        userInfo.setLastLoginIp("");
        userInfo.setLastLoginTime(LocalDateTime.now());
        userInfoService.updateById(userInfo);
    }
}
