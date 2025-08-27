package com.glowxq.system.admin.service.impl;

import com.glowxq.core.common.entity.ImportExcelDTO;
import com.glowxq.core.common.entity.PageResult;
import com.glowxq.core.common.entity.SelectIdsDTO;
import com.glowxq.core.common.enums.CommonResponseEnum;
import com.glowxq.core.util.BeanCopyUtils;
import com.glowxq.core.util.FileUtils;
import com.glowxq.core.util.PageUtils;
import com.glowxq.core.util.Utils;
import com.glowxq.excel.core.ExcelResult;
import com.glowxq.excel.utils.ExcelUtils;
import com.glowxq.system.admin.mapper.SysOperationLogMapper;
import com.glowxq.system.admin.pojo.dto.SysOperationLogCreateDTO;
import com.glowxq.system.admin.pojo.dto.SysOperationLogImportDTO;
import com.glowxq.system.admin.pojo.dto.SysOperationLogListDTO;
import com.glowxq.system.admin.pojo.dto.SysOperationLogUpdateDTO;
import com.glowxq.system.admin.pojo.po.SysOperationLog;
import com.glowxq.system.admin.pojo.vo.SysOperationLogVO;
import com.glowxq.system.admin.service.SysOperationLogService;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 操作日志 服务实现类
 * </p>
 *
 * @author glowxq
 * @since 2025-06-15
 */
@Service
@RequiredArgsConstructor
public class SysOperationLogServiceImpl extends ServiceImpl<SysOperationLogMapper, SysOperationLog> implements SysOperationLogService {

    private static QueryWrapper buildQueryWrapper(SysOperationLogListDTO dto) {
        QueryWrapper wrapper = QueryWrapper.create().from(SysOperationLog.class);
        wrapper.eq(SysOperationLog::getUserId, dto.getUserId(), Utils.isNotNull(dto.getUserId()));
        wrapper.eq(SysOperationLog::getTraceId, dto.getTraceId(), Utils.isNotNull(dto.getTraceId()));
        wrapper.eq(SysOperationLog::getSpanId, dto.getSpanId(), Utils.isNotNull(dto.getSpanId()));
        wrapper.like(SysOperationLog::getUsername, dto.getUsername(), Utils.isNotNull(dto.getUsername()));
        wrapper.eq(SysOperationLog::getIp, dto.getIp(), Utils.isNotNull(dto.getIp()));
        wrapper.eq(SysOperationLog::getMethod, dto.getMethod(), Utils.isNotNull(dto.getMethod()));
        wrapper.eq(SysOperationLog::getUri, dto.getUri(), Utils.isNotNull(dto.getUri()));
        wrapper.eq(SysOperationLog::getHeader, dto.getHeader(), Utils.isNotNull(dto.getHeader()));
        wrapper.eq(SysOperationLog::getModule, dto.getModule(), Utils.isNotNull(dto.getModule()));
        wrapper.eq(SysOperationLog::getDescription, dto.getDescription(), Utils.isNotNull(dto.getDescription()));
        wrapper.eq(SysOperationLog::getParam, dto.getParam(), Utils.isNotNull(dto.getParam()));
        wrapper.eq(SysOperationLog::getRequest, dto.getRequest(), Utils.isNotNull(dto.getRequest()));
        wrapper.eq(SysOperationLog::getResponse, dto.getResponse(), Utils.isNotNull(dto.getResponse()));
        wrapper.eq(SysOperationLog::getError, dto.getError(), Utils.isNotNull(dto.getError()));
        wrapper.eq(SysOperationLog::getBusinessType, dto.getBusinessType(), Utils.isNotNull(dto.getBusinessType()));
        wrapper.eq(SysOperationLog::getErrorMessage, dto.getErrorMessage(), Utils.isNotNull(dto.getErrorMessage()));
        wrapper.eq(SysOperationLog::getCostTime, dto.getCostTime(), Utils.isNotNull(dto.getCostTime()));
        wrapper.eq(SysOperationLog::getTenantId, dto.getTenantId(), Utils.isNotNull(dto.getTenantId()));
        return wrapper;
    }

    @Override
    public void create(SysOperationLogCreateDTO dto) {
        SysOperationLog sysOperationLog = BeanCopyUtils.copy(dto, SysOperationLog.class);
        save(sysOperationLog);
    }

    @Override
    public void update(SysOperationLogUpdateDTO dto) {
        SysOperationLog sysOperationLog = BeanCopyUtils.copy(dto, SysOperationLog.class);
        QueryWrapper wrapper;
        // id有效性校验
        wrapper = QueryWrapper.create()
                              .eq(SysOperationLog::getId, dto.getId());
        CommonResponseEnum.INVALID_ID.assertTrue(count(wrapper) <= 0);

        saveOrUpdate(sysOperationLog);
    }

    @Override
    public PageResult<SysOperationLogVO> page(SysOperationLogListDTO dto) {
        Page<SysOperationLogVO> page = pageAs(PageUtils.getPage(dto), buildQueryWrapper(dto), SysOperationLogVO.class);
        return PageUtils.getPageResult(page);
    }

    @Override
    public List<SysOperationLogVO> list(SysOperationLogListDTO dto) {
        return listAs(buildQueryWrapper(dto), SysOperationLogVO.class);
    }

    @Override
    public void remove(SelectIdsDTO dto) {
        CommonResponseEnum.INVALID_ID.assertTrue(dto.getIds().isEmpty());
        removeByIds(dto.getIds());
    }

    @Override
    public SysOperationLogVO detail(Long id) {
        SysOperationLog sysOperationLog = getById((Serializable) id);
        CommonResponseEnum.INVALID_ID.assertNull(sysOperationLog);
        return BeanCopyUtils.copy(sysOperationLog, SysOperationLogVO.class);
    }

    @SneakyThrows
    @Override
    public void importExcel(ImportExcelDTO dto) {
        ExcelResult<SysOperationLogImportDTO> excelResult = ExcelUtils.importExcel(dto.getFile().getInputStream(), SysOperationLogImportDTO.class, true);
        List<SysOperationLogImportDTO> list = excelResult.getList();
        List<String> errorList = excelResult.getErrorList();
        String analysis = excelResult.getAnalysis();
        System.out.println(" analysis : " + analysis);
        System.out.println(" isCover : " + dto.getIsCover());
    }

    @SneakyThrows
    @Override
    public void exportExcel(SysOperationLogListDTO dto, HttpServletResponse response) {
        List<SysOperationLogVO> list = list(dto);
        String fileName = "操作日志模板";
        OutputStream os = FileUtils.getOutputStream(response, fileName + ".xlsx");
        ExcelUtils.exportExcel(list, "操作日志", SysOperationLogVO.class, os);
    }

    @Override
    public void handleLog(SysOperationLog operLog) {
        Thread.ofVirtual().start(() -> {
            mapper.insert(operLog, true);
        });
    }
}