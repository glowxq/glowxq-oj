package com.glowxq.system.admin.service;

import com.glowxq.core.common.entity.PageResult;
import com.glowxq.system.admin.pojo.dto.systempfile.SysTempFileHistoryCreateDTO;
import com.glowxq.system.admin.pojo.dto.systempfile.SysTempFileHistoryListDTO;
import com.glowxq.system.admin.pojo.po.SysTempFileHistory;
import com.mybatisflex.core.service.IService;

/**
 * <p>
 * 模版文件历史 Service
 * </p>
 *
 * @author glowxq-admin
 * @since 2024-12-05
 */
public interface SysTempFileHistoryService extends IService<SysTempFileHistory> {

    void create(SysTempFileHistoryCreateDTO dto);

    PageResult<SysTempFileHistory> historyList(SysTempFileHistoryListDTO dto);
}