package com.glowxq.system.applet.mapper;

import com.glowxq.system.applet.pojo.po.AppletUser;
import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.core.query.QueryWrapper;

/**
 * <p>
 * 小程序用户 Mapper 接口
 * </p>
 *
 * @author glowxq
 * @since 2025-04-26
 */
public interface AppletUserMapper extends BaseMapper<AppletUser> {

    default AppletUser getByOpenId(String openid) {
        QueryWrapper qw = QueryChain.create();
        qw.eq(AppletUser::getOpenid, openid);
        return selectOneByQuery(qw);
    }

    default AppletUser getByBindCode(String bindCode) {
        QueryWrapper qw = QueryChain.create();
        qw.eq(AppletUser::getBindCode, bindCode);
        return selectOneByQuery(qw);
    }

    default AppletUser getByUsername(String username) {
        QueryWrapper qw = QueryChain.create();
        qw.eq(AppletUser::getUsername, username);
        return selectOneByQuery(qw);
    }

    default AppletUser getByCode(String bindCode) {
        QueryWrapper qw = QueryChain.create();
        qw.eq(AppletUser::getCode, bindCode);
        return selectOneByQuery(qw);
    }
}