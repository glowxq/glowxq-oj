package com.glowxq.system.admin.service.impl;

import com.glowxq.core.common.entity.PageResult;
import com.glowxq.core.common.entity.SelectIdsDTO;
import com.glowxq.core.common.enums.CommonResponseEnum;
import com.glowxq.core.common.service.ConfService;
import com.glowxq.core.util.BeanCopyUtils;
import com.glowxq.core.util.PageUtils;
import com.glowxq.core.util.Utils;
import com.glowxq.redis.RedisCache;
import com.glowxq.system.admin.mapper.SysConfigMapper;
import com.glowxq.system.admin.pojo.dto.sysconfig.SysConfigCreateDTO;
import com.glowxq.system.admin.pojo.dto.sysconfig.SysConfigListDTO;
import com.glowxq.system.admin.pojo.dto.sysconfig.SysConfigUpdateDTO;
import com.glowxq.system.admin.pojo.po.SysConfig;
import com.glowxq.system.admin.pojo.po.table.SysConfigTableDef;
import com.glowxq.system.admin.service.SysConfigService;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 参数配置表 服务实现类
 * </p>
 *
 * @author glowxq
 * @since 2023-11-23
 */
@Service
@RequiredArgsConstructor
public class SysConfigServiceImpl extends ServiceImpl<SysConfigMapper, SysConfig> implements SysConfigService, ConfService {

    private final RedisCache redisCache;

    @Override
    public void create(SysConfigCreateDTO dto) {
        SysConfig sysConfig = BeanCopyUtils.copy(dto, SysConfig.class);
        QueryWrapper wrapper = QueryWrapper.create().where(SysConfigTableDef.SYS_CONFIG.CONFIG_KEY.eq(sysConfig.getConfigKey()));
        CommonResponseEnum.EXISTS.message("key已存在").assertTrue(count(wrapper) > 0);
        save(sysConfig);
    }

    @Override
    public void update(SysConfigUpdateDTO dto) {
        SysConfig sysConfig = BeanCopyUtils.copy(dto, SysConfig.class);
        QueryWrapper wrapper = QueryWrapper.create().where(SysConfigTableDef.SYS_CONFIG.ID.ne(dto.getId()))
                .where(SysConfigTableDef.SYS_CONFIG.CONFIG_KEY.eq(dto.getConfigKey()));
        CommonResponseEnum.EXISTS.message(2015, "key已存在").assertTrue(count(wrapper) > 0);
        saveOrUpdate(sysConfig);
        redisCache.clearConf(sysConfig.getConfigKey()); // 清除conf key
    }

    @Override
    public PageResult<SysConfig> list(SysConfigListDTO dto) {
        QueryWrapper wrapper = QueryWrapper.create();
        if (Utils.isNotNull(dto.getConfigName())) {
            wrapper.where(SysConfigTableDef.SYS_CONFIG.CONFIG_NAME.like(dto.getConfigName()));
        }
        if (Utils.isNotNull(dto.getConfigKey())) {
            wrapper.where(SysConfigTableDef.SYS_CONFIG.CONFIG_KEY.like(dto.getConfigKey()));
        }
        wrapper.orderBy(SysConfigTableDef.SYS_CONFIG.CREATE_TIME.asc());
        return PageUtils.getPageResult(page(PageUtils.getPage(dto), wrapper));
    }

    @Override
    public void remove(SelectIdsDTO dto) {
        QueryWrapper wrapper = QueryWrapper.create().where(SysConfigTableDef.SYS_CONFIG.ID.in(dto.getIds()));
        List<SysConfig> list = list(wrapper);
        for (SysConfig sysConfig : list) {
            redisCache.clearConf(sysConfig.getConfigKey()); // 清除conf key
        }
        removeByIds(dto.getIds());
    }

    @Override
    public SysConfig detail(Object id) {
        return getById((Serializable) id);
    }

    @Override
    public boolean hasConfKey(String key) {
        return redisCache.hasConfKey(key);
    }

    @Override
    public String getConfValue(String key) {
        if (hasConfKey(key)) {
            return redisCache.getConfValue(key);
        } else {
            QueryWrapper wrapper = QueryWrapper.create().where(SysConfigTableDef.SYS_CONFIG.CONFIG_KEY.eq(key));
            SysConfig sysConfig = getOne(wrapper);
            if (sysConfig != null) {
                String value = sysConfig.getConfigValue();
                redisCache.putConf(key, value);
                return value;
            }
        }
        return "";
    }
}
