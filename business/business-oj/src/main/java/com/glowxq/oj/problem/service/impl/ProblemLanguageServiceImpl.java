package com.glowxq.oj.problem.service.impl;

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
import com.glowxq.oj.problem.mapper.ProblemLanguageMapper;
import com.glowxq.oj.problem.pojo.dto.ProblemLanguageCreateDTO;
import com.glowxq.oj.problem.pojo.dto.ProblemLanguageImportDTO;
import com.glowxq.oj.problem.pojo.dto.ProblemLanguageListDTO;
import com.glowxq.oj.problem.pojo.dto.ProblemLanguageUpdateDTO;
import com.glowxq.oj.problem.pojo.po.ProblemLanguage;
import com.glowxq.oj.problem.pojo.vo.ProblemLanguageVO;
import com.glowxq.oj.problem.service.ProblemLanguageService;
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

/**
 * <p>
 * 题目语言 服务实现类
 * </p>
 *
 * @author glowxq
 * @since 2025-03-17
 */
@Service
@RequiredArgsConstructor
public class ProblemLanguageServiceImpl extends ServiceImpl<ProblemLanguageMapper, ProblemLanguage> implements ProblemLanguageService {
    @Override
    public void create(ProblemLanguageCreateDTO dto){
        ProblemLanguage problemLanguage = BeanCopyUtils.copy(dto, ProblemLanguage.class);
        save(problemLanguage);
    }

    @Override
    public void update(ProblemLanguageUpdateDTO dto){
        ProblemLanguage problemLanguage = BeanCopyUtils.copy(dto, ProblemLanguage.class);
        QueryWrapper wrapper;
        // id有效性校验
        wrapper = QueryWrapper.create()
            .eq(ProblemLanguage::getId, dto.getId());
        CommonResponseEnum.INVALID_ID.assertTrue(count(wrapper) <= 0);

        saveOrUpdate(problemLanguage);
    }

    @Override
    public PageResult<ProblemLanguageVO> page(ProblemLanguageListDTO dto){
        Page<ProblemLanguageVO> page = pageAs(PageUtils.getPage(dto), buildQueryWrapper(dto), ProblemLanguageVO.class);
        return PageUtils.getPageResult(page);
    }

    @Override
    public List<ProblemLanguageVO> list(ProblemLanguageListDTO dto){
        return listAs(buildQueryWrapper(dto), ProblemLanguageVO.class);
    }

    @Override
    public void remove(SelectIdsDTO dto){
        CommonResponseEnum.INVALID_ID.assertTrue(dto.getIds().isEmpty());
        removeByIds(dto.getIds());
    }

    @Override
    public ProblemLanguageVO detail(Object id){
        ProblemLanguage problemLanguage = getById((Serializable) id);
        CommonResponseEnum.INVALID_ID.assertNull(problemLanguage);
        return BeanCopyUtils.copy(problemLanguage, ProblemLanguageVO.class);
    }

    @SneakyThrows
    @Override
    public void importExcel(ImportExcelDTO dto) {
        ExcelResult<ProblemLanguageImportDTO> excelResult = ExcelUtils.importExcel(dto.getFile().getInputStream(), ProblemLanguageImportDTO.class, true);
        List<ProblemLanguageImportDTO> list = excelResult.getList();
        List<String> errorList = excelResult.getErrorList();
        String analysis = excelResult.getAnalysis();
        System.out.println(" analysis : " + analysis);
        System.out.println(" isCover : " + dto.getIsCover());
    }

    @SneakyThrows
    @Override
    public void exportExcel(ProblemLanguageListDTO dto, HttpServletResponse response) {
        List<ProblemLanguageVO> list = list(dto);
        String fileName = "题目语言模板";
        OutputStream os = FileUtils.getOutputStream(response, fileName + ".xlsx");
        ExcelUtils.exportExcel(list, "题目语言", ProblemLanguageVO.class, os);
    }

    @Override
    public void bindLanguageIds(Long problemId, List<Long> languageIds) {
        if (CollectionUtils.isEmpty(languageIds)|| problemId == null){
            return;
        }
        List<ProblemLanguage> problemLanguages = languageIds.stream().map(languageId -> {
            ProblemLanguage problemLanguage = new ProblemLanguage();
            problemLanguage.setProblemId(problemId);
            problemLanguage.setLanguageId(languageId);
            problemLanguage.setProblemKey("");
            problemLanguage.setProblemLanguage("");
            return problemLanguage;
        }).toList();
        mapper.insertBatch(problemLanguages);
    }

    @Override
    public void deleteByProblemId(Long problemId) {
        mapper.deleteByProblemId(problemId);
    }

    private static QueryWrapper buildQueryWrapper(ProblemLanguageListDTO dto) {
        QueryWrapper wrapper = QueryWrapper.create().from(ProblemLanguage.class);
        wrapper.eq(ProblemLanguage::getProblemId, dto.getProblemId(), Utils.isNotNull(dto.getProblemId()));
        wrapper.eq(ProblemLanguage::getProblemKey, dto.getProblemKey(), Utils.isNotNull(dto.getProblemKey()));
        wrapper.eq(ProblemLanguage::getProblemLanguage, dto.getProblemLanguage(), Utils.isNotNull(dto.getProblemLanguage()));
        wrapper.eq(ProblemLanguage::getLanguageId, dto.getLanguageId(), Utils.isNotNull(dto.getLanguageId()));
        return wrapper;
    }
}