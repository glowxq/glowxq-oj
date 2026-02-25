package com.glowxq.system.admin.service;

import com.glowxq.core.common.entity.PageResult;
import com.glowxq.core.common.entity.SelectIdsDTO;
import com.glowxq.system.admin.pojo.dto.sysconfig.SysConfigCreateDTO;
import com.glowxq.system.admin.pojo.dto.sysconfig.SysConfigListDTO;
import com.glowxq.system.admin.pojo.dto.sysconfig.SysConfigUpdateDTO;
import com.glowxq.system.admin.pojo.po.SysConfig;
import com.mybatisflex.core.service.IService;

/**
 * <p>
 * 参数配置表 服务类
 * </p>
 *
 * @author glowxq
 * @since 2023-11-23
 */
public interface SysConfigService extends IService<SysConfig> {

    void create(SysConfigCreateDTO dto);

    void update(SysConfigUpdateDTO dto);

    PageResult<SysConfig> list(SysConfigListDTO queryDTO);

    void remove(SelectIdsDTO dto);

    SysConfig detail(Object id);
}
