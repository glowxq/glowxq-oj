package com.glowxq.platform.loader.dynamic;

import com.glowxq.core.common.entity.DictVO;
import com.glowxq.platform.enums.DynamicDictEnum;
import com.glowxq.platform.loader.DictLoader;
import com.glowxq.redis.RedisCache;
import com.glowxq.system.admin.pojo.vo.sysuser.UserOptionVO;
import com.glowxq.system.admin.service.SysUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 动态字典——系统用户loader
 *
 * DynamicUserOptionDictLoader
 * 
 * @author glowxq
 * @since 2024/8/21 13:17
 * @version 1.0
 */
@Component
@RequiredArgsConstructor
public class UserOptionDictLoader implements DictLoader {

    private final RedisCache redisCache;

    private final SysUserService sysUserService;

    @Override
    public DynamicDictEnum getDynamicTypeCode() {
        return DynamicDictEnum.DYNAMIC_USER_OPTIONS;
    }

    @Override
    public Map<String, List<DictVO>> loadDict() {
        String key = getDynamicTypeCode().getTypeCode();
        String name = getDynamicTypeCode().getName();
        if (redisCache.hasHashKey(key)) {
            return Map.of(key, redisCache.getDictByType(key));
        }

        DictVO dictVO;
        List<DictVO> list = new ArrayList<>();
        List<UserOptionVO> userOptions = sysUserService.getUserOptions();
        for (int i = 0; i < userOptions.size(); i++) {
            UserOptionVO option = userOptions.get(i);
            dictVO = DictVO.builder().id(option.getId().toString()).codeName(option.getNickname()).alias(option.getUsername()).sort(i + 1).sysDictTypeCode(key)
                    .sysDictTypeName(name).callbackShowStyle("primary").isDynamic(true).isLock("F").isShow("T").build();
            list.add(dictVO);
        }
        redisCache.setDict(key, list);
        return Map.of(key, list);
    }

    @Override
    public List<DictVO> getDict(String typeCode) {
        return loadDict().get(typeCode);
    }

}
