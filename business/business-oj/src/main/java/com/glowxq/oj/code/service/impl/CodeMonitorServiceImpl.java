package com.glowxq.oj.code.service.impl;

import com.glowxq.core.common.entity.*;
import com.glowxq.core.common.enums.CommonResponseEnum;
import com.glowxq.core.common.enums.WebsocketBusinessType;
import com.glowxq.core.util.*;
import com.glowxq.excel.core.ExcelResult;
import com.glowxq.excel.utils.ExcelUtils;
import com.glowxq.oj.code.enums.CodeMonitorStatus;
import com.glowxq.oj.code.mapper.CodeMonitorMapper;
import com.glowxq.oj.code.pojo.dto.*;
import com.glowxq.oj.code.pojo.po.CodeMonitor;
import com.glowxq.oj.code.pojo.vo.CodeMonitorVO;
import com.glowxq.oj.code.service.CodeMonitorService;

import com.glowxq.oj.user.pojo.po.UserInfo;
import com.glowxq.security.core.util.LoginUtils;
import com.glowxq.system.meta.enums.SystemTagBind;
import com.glowxq.websocket.stc.ServiceToClientMessageHandler;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 代码监控 服务实现类
 * </p>
 *
 * @author glowxq
 * @since 2025-04-04
 */
@Service
@RequiredArgsConstructor
public class CodeMonitorServiceImpl extends ServiceImpl<CodeMonitorMapper, CodeMonitor> implements CodeMonitorService {



    private final ServiceToClientMessageHandler serviceToClientMessageHandler;

    private static QueryWrapper buildQueryWrapper(CodeMonitorListDTO dto) {
        QueryWrapper wrapper = QueryWrapper.create().from(CodeMonitor.class);
        wrapper.eq(CodeMonitor::getMonitorUserId, dto.getMonitorUserId(), Utils.isNotNull(dto.getMonitorUserId()));
        wrapper.eq(CodeMonitor::getOverlayUserId, dto.getOverlayUserId(), Utils.isNotNull(dto.getOverlayUserId()));
        wrapper.eq(CodeMonitor::getMonitorPhone, dto.getMonitorPhone(), Utils.isNotNull(dto.getMonitorPhone()));
        wrapper.eq(CodeMonitor::getOverlayPhone, dto.getOverlayPhone(), Utils.isNotNull(dto.getOverlayPhone()));
        wrapper.like(CodeMonitor::getMonitorName, dto.getMonitorName(), Utils.isNotNull(dto.getMonitorName()));
        wrapper.like(CodeMonitor::getOverlayName, dto.getOverlayName(), Utils.isNotNull(dto.getOverlayName()));
        wrapper.eq(CodeMonitor::getMonitorCode, dto.getMonitorCode(), Utils.isNotNull(dto.getMonitorCode()));
        wrapper.eq(CodeMonitor::getOverlayCode, dto.getOverlayCode(), Utils.isNotNull(dto.getOverlayCode()));
        wrapper.eq(CodeMonitor::getCodeMode, dto.getCodeMode(), Utils.isNotNull(dto.getCodeMode()));
        wrapper.eq(CodeMonitor::getMonitorStatus, dto.getMonitorStatus(), Utils.isNotNull(dto.getMonitorStatus()));
        wrapper.eq(CodeMonitor::getVersion, dto.getVersion(), Utils.isNotNull(dto.getVersion()));
        if (StringUtils.isNotBlank(dto.getSearchKey())) {
            wrapper.or(queryWrapper -> {
                queryWrapper.like(CodeMonitor::getMonitorName, dto.getSearchKey());
                queryWrapper.like(CodeMonitor::getMonitorPhone, dto.getSearchKey());
            });
        }
        wrapper.orderBy(CodeMonitor::getVersion).desc();
        return wrapper;
    }

    @Override
    public void monitorCode(CodeMonitorDTO dto, UserInfo userInfo) {
        CodeMonitor codeMonitor = mapper.getByUserId(userInfo.getId(), dto.getCodeMode());
        if (codeMonitor == null) {
            codeMonitor = BeanCopyUtils.copy(dto, CodeMonitor.class);
            codeMonitor.setMonitorUserId(userInfo.getId());
            codeMonitor.setMonitorPhone(userInfo.getPhone());
            codeMonitor.setMonitorName(userInfo.getName());
            codeMonitor.setMonitorCode(dto.getMonitorCode());
            codeMonitor.setOverlayUserId(0L);
            codeMonitor.setOverlayPhone("");
            codeMonitor.setOverlayName("");
            codeMonitor.setOverlayCode("");
            codeMonitor.setCodeMode(dto.getCodeMode());
            codeMonitor.setMonitorStatus(CodeMonitorStatus.MonitorPush.getCode());
            codeMonitor.setVersion(1);
            mapper.insert(codeMonitor, true);
        }
        else {
            codeMonitor.setMonitorUserId(userInfo.getId());
            codeMonitor.setMonitorPhone(userInfo.getPhone());
            codeMonitor.setMonitorName(userInfo.getName());
            codeMonitor.setMonitorCode(dto.getMonitorCode());
            codeMonitor.setMonitorStatus(CodeMonitorStatus.MonitorPush.getCode());
            codeMonitor.setVersion(codeMonitor.getVersion() + 1);
            mapper.update(codeMonitor);
        }
    }

    @Override
    public void coveredPush(CoveredCodeMonitorDTO dto) {
        BaseUserInfo userInfo = Objects.requireNonNull(LoginUtils.getLoginUser()).getUserInfo();
        CodeMonitor codeMonitor = mapper.selectOneById(dto.getId());
        CommonResponseEnum.INVALID_ID.assertNull(codeMonitor);
        codeMonitor.setOverlayUserId(userInfo.getId());
        codeMonitor.setOverlayPhone(userInfo.getPhone());
        codeMonitor.setOverlayName(userInfo.getName());
        codeMonitor.setOverlayCode(dto.getOverlayCode());
        codeMonitor.setVersion(codeMonitor.getVersion() + 1);
        mapper.update(codeMonitor);

        // 发送推送消息
        SocketMessage<CodeMonitor> socketMessage = new SocketMessage<>(codeMonitor, WebsocketBusinessType.PushCoveredCode);
        TransferMessage<CodeMonitor> transferMessage = new TransferMessage<>();
        transferMessage.setToPushAll(false);
        transferMessage.toUsers(codeMonitor.getMonitorUserId().toString());
        transferMessage.setMessage(socketMessage);
        transferMessage.setFromUser("System");
        serviceToClientMessageHandler.handleTransferMessage(transferMessage);
    }

    @Override
    public PageResult<CodeMonitorVO> page(CodeMonitorListDTO dto) {
        QueryWrapper queryWrapper = buildQueryWrapper(dto);

        Page<CodeMonitorVO> page = pageAs(PageUtils.getPage(dto), queryWrapper, CodeMonitorVO.class);
        return PageUtils.getPageResult(page);
    }

    @Override
    public List<CodeMonitorVO> list(CodeMonitorListDTO dto) {
        return listAs(buildQueryWrapper(dto), CodeMonitorVO.class);
    }

    @Override
    public void remove(SelectIdsDTO dto) {
        CommonResponseEnum.INVALID_ID.assertTrue(dto.getIds().isEmpty());
        removeByIds(dto.getIds());
    }

    @Override
    public CodeMonitorVO detail(Object id) {
        CodeMonitor codeMonitor = getById((Serializable) id);
        CommonResponseEnum.INVALID_ID.assertNull(codeMonitor);
        return BeanCopyUtils.copy(codeMonitor, CodeMonitorVO.class);
    }

    @SneakyThrows
    @Override
    public void importExcel(ImportExcelDTO dto) {
        ExcelResult<CodeMonitorImportDTO> excelResult = ExcelUtils.importExcel(dto.getFile().getInputStream(), CodeMonitorImportDTO.class, true);
        List<CodeMonitorImportDTO> list = excelResult.getList();
        List<String> errorList = excelResult.getErrorList();
        String analysis = excelResult.getAnalysis();
        System.out.println(" analysis : " + analysis);
        System.out.println(" isCover : " + dto.getIsCover());
    }

    @SneakyThrows
    @Override
    public void exportExcel(CodeMonitorListDTO dto, HttpServletResponse response) {
        List<CodeMonitorVO> list = list(dto);
        String fileName = "代码监控模板";
        OutputStream os = FileUtils.getOutputStream(response, fileName + ".xlsx");
        ExcelUtils.exportExcel(list, "代码监控", CodeMonitorVO.class, os);
    }

    @Override
    public void coveredHandle(CoveredHandleDTO dto) {
        CodeMonitor codeMonitor = mapper.selectOneById(dto.getId());
        codeMonitor.setMonitorStatus(dto.getMonitorStatus());
        mapper.update(codeMonitor, true);
    }

    @Override
    public List<CodeMonitor> listByUserIds(List<Long> userIds) {
        return mapper.listByUserIds(userIds);
    }

    @Override
    public List<CodeMonitor> listByGroupId(Long groupId) {
        return List.of();
    }

    @Override
    public List<CodeMonitor> searchMonitorCodeList(SearchMonitorCodeDTO dto) {
        return mapper.searchMonitorCodeList(dto);
    }
}