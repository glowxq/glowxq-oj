package com.glowxq.oj.code.service;

import com.glowxq.core.common.entity.ImportExcelDTO;
import com.glowxq.core.common.entity.PageResult;
import com.glowxq.core.common.entity.SelectIdsDTO;
import com.glowxq.oj.code.pojo.dto.*;
import com.glowxq.oj.code.pojo.po.CodeMonitor;
import com.glowxq.oj.code.pojo.vo.CodeMonitorVO;
import com.glowxq.oj.user.pojo.po.UserInfo;
import com.mybatisflex.core.service.IService;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

/**
 * <p>
 * 代码监控 Service
 * </p>
 *
 * @author glowxq
 * @since 2025-04-04
 */
public interface CodeMonitorService extends IService<CodeMonitor> {

    void monitorCode(CodeMonitorDTO dto, UserInfo userInfo);

    void coveredPush(CoveredCodeMonitorDTO dto);

    PageResult<CodeMonitorVO> page(CodeMonitorListDTO dto);

    List<CodeMonitorVO> list(CodeMonitorListDTO dto);

    void remove(SelectIdsDTO dto);

    CodeMonitorVO detail(Object id);

    void importExcel(ImportExcelDTO dto);

    void exportExcel(CodeMonitorListDTO dto, HttpServletResponse response);

    void coveredHandle(CoveredHandleDTO dto);

    List<CodeMonitor> listByUserIds(List<Long> userIds);

    List<CodeMonitor> listByGroupId(Long groupId);

    List<CodeMonitor> searchMonitorCodeList(SearchMonitorCodeDTO dto);
}