package com.glowxq.system.admin.service;

import com.glowxq.core.common.entity.PageResult;
import com.glowxq.core.common.entity.SelectIdsDTO;
import com.glowxq.security.pojo.ClientVO;
import com.glowxq.system.admin.pojo.dto.sysclient.SysClientCreateDTO;
import com.glowxq.system.admin.pojo.dto.sysclient.SysClientListDTO;
import com.glowxq.system.admin.pojo.dto.sysclient.SysClientUpdateDTO;
import com.glowxq.system.admin.pojo.po.SysClient;
import com.glowxq.system.admin.pojo.vo.sysclient.SysClientVO;
import com.mybatisflex.core.service.IService;

import java.util.List;

/**
 * <p>
 * 系统授权表 Service
 * </p>
 *
 * @author glowxq
 * @since 2024-01-22
 */
public interface SysClientService extends IService<SysClient> {

    void create(SysClientCreateDTO dto);

    void update(SysClientUpdateDTO dto);

    PageResult<SysClientVO> page(SysClientListDTO dto);

    List<SysClientVO> list(SysClientListDTO dto);

    void remove(SelectIdsDTO dto);

    ClientVO detail(Object id);

}