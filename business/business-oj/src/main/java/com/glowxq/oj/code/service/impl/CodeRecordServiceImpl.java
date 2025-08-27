package com.glowxq.oj.code.service.impl;

import com.glowxq.core.common.entity.BaseUserInfo;
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
import com.glowxq.oj.code.mapper.CodeRecordMapper;
import com.glowxq.oj.code.pojo.dto.CodeRecordCreateDTO;
import com.glowxq.oj.code.pojo.dto.CodeRecordImportDTO;
import com.glowxq.oj.code.pojo.dto.CodeRecordListDTO;
import com.glowxq.oj.code.pojo.dto.CodeRecordUpdateDTO;
import com.glowxq.oj.code.pojo.po.CodeRecord;
import com.glowxq.oj.code.pojo.vo.CodeRecordVO;
import com.glowxq.oj.code.service.CodeRecordService;

import com.glowxq.security.core.util.LoginUtils;
import com.glowxq.system.meta.enums.SystemTagBind;
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
 * 用户代码 服务实现类
 * </p>
 *
 * @author glowxq
 * @since 2025-04-04
 */
@Service
@RequiredArgsConstructor
public class CodeRecordServiceImpl extends ServiceImpl<CodeRecordMapper, CodeRecord> implements CodeRecordService {



    private static QueryWrapper buildQueryWrapper(CodeRecordListDTO dto) {
        QueryWrapper wrapper = QueryWrapper.create().from(CodeRecord.class);
        wrapper.eq(CodeRecord::getUserId, dto.getUserId(), Utils.isNotNull(dto.getUserId()));
        wrapper.like(CodeRecord::getName, dto.getName(), Utils.isNotNull(dto.getName()));
        wrapper.like(CodeRecord::getUsername, dto.getUsername(), Utils.isNotNull(dto.getUsername()));
        wrapper.eq(CodeRecord::getCode, dto.getCode(), Utils.isNotNull(dto.getCode()));
        wrapper.eq(CodeRecord::getCodeMode, dto.getCodeMode(), Utils.isNotNull(dto.getCodeMode()));
        wrapper.orderBy(CodeRecord::getId).desc();
        return wrapper;
    }

    @Override
    public void create(CodeRecordCreateDTO dto) {
        BaseUserInfo userInfo = Objects.requireNonNull(LoginUtils.getLoginUser()).getUserInfo();
        CodeRecord codeRecord = BeanCopyUtils.copy(dto, CodeRecord.class);
        codeRecord.setUserId(userInfo.getId());
        codeRecord.setName(userInfo.getName());
        codeRecord.setUsername(userInfo.getUsername());
        codeRecord.setCode(dto.getCode());
        codeRecord.setCodeMode(dto.getCodeMode());
        mapper.insert(codeRecord, true);
    }

    @Override
    public void update(CodeRecordUpdateDTO dto) {
        CodeRecord codeRecord = BeanCopyUtils.copy(dto, CodeRecord.class);
        QueryWrapper wrapper;
        // id有效性校验
        wrapper = QueryWrapper.create()
                              .eq(CodeRecord::getId, dto.getId());
        CommonResponseEnum.INVALID_ID.assertTrue(count(wrapper) <= 0);

        saveOrUpdate(codeRecord);
    }

    @Override
    public PageResult<CodeRecordVO> page(CodeRecordListDTO dto) {
        QueryWrapper queryWrapper = buildQueryWrapper(dto);

        Page<CodeRecordVO> page = pageAs(PageUtils.getPage(dto), queryWrapper, CodeRecordVO.class);
        return PageUtils.getPageResult(page);
    }

    @Override
    public List<CodeRecordVO> list(CodeRecordListDTO dto) {
        return listAs(buildQueryWrapper(dto), CodeRecordVO.class);
    }

    @Override
    public void remove(SelectIdsDTO dto) {
        CommonResponseEnum.INVALID_ID.assertTrue(dto.getIds().isEmpty());
        removeByIds(dto.getIds());
    }

    @Override
    public CodeRecordVO detail(Object id) {
        CodeRecord codeRecord = getById((Serializable) id);
        CommonResponseEnum.INVALID_ID.assertNull(codeRecord);
        return BeanCopyUtils.copy(codeRecord, CodeRecordVO.class);
    }

    @SneakyThrows
    @Override
    public void importExcel(ImportExcelDTO dto) {
        ExcelResult<CodeRecordImportDTO> excelResult = ExcelUtils.importExcel(dto.getFile().getInputStream(), CodeRecordImportDTO.class, true);
        List<CodeRecordImportDTO> list = excelResult.getList();
        List<String> errorList = excelResult.getErrorList();
        String analysis = excelResult.getAnalysis();
        System.out.println(" analysis : " + analysis);
        System.out.println(" isCover : " + dto.getIsCover());
    }

    @SneakyThrows
    @Override
    public void exportExcel(CodeRecordListDTO dto, HttpServletResponse response) {
        List<CodeRecordVO> list = list(dto);
        String fileName = "用户代码模板";
        OutputStream os = FileUtils.getOutputStream(response, fileName + ".xlsx");
        ExcelUtils.exportExcel(list, "用户代码", CodeRecordVO.class, os);
    }

    @Override
    public List<CodeRecord> listByUserId(Long userId) {
        return mapper.listByUserId(userId);
    }
}