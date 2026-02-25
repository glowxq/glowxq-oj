package com.glowxq.system.admin.service;

import com.glowxq.core.common.entity.ImportExcelDTO;
import com.glowxq.core.common.entity.PageResult;
import com.glowxq.core.common.entity.SelectIdsDTO;
import com.glowxq.system.admin.pojo.dto.SysOperationLogCreateDTO;
import com.glowxq.system.admin.pojo.dto.SysOperationLogListDTO;
import com.glowxq.system.admin.pojo.dto.SysOperationLogUpdateDTO;
import com.glowxq.system.admin.pojo.po.SysOperationLog;
import com.glowxq.system.admin.pojo.vo.SysOperationLogVO;
import com.mybatisflex.core.service.IService;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

/**
 * <p>
 * 操作日志 Service
 * </p>
 *
 * @author glowxq
 * @since 2025-06-15
 */
public interface SysOperationLogService extends IService<SysOperationLog> {

    void create(SysOperationLogCreateDTO dto);

    void update(SysOperationLogUpdateDTO dto);

    PageResult<SysOperationLogVO> page(SysOperationLogListDTO dto);

    List<SysOperationLogVO> list(SysOperationLogListDTO dto);

    void remove(SelectIdsDTO dto);

    SysOperationLogVO detail(Long id);

    void importExcel(ImportExcelDTO dto);

    void exportExcel(SysOperationLogListDTO dto, HttpServletResponse response);

    void handleLog(SysOperationLog operLog);
}