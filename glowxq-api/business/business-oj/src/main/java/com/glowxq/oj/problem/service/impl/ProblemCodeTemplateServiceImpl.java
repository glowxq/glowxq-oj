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
import com.glowxq.oj.problem.mapper.ProblemCodeTemplateMapper;
import com.glowxq.oj.problem.pojo.dto.ProblemCodeTemplateCreateDTO;
import com.glowxq.oj.problem.pojo.dto.ProblemCodeTemplateImportDTO;
import com.glowxq.oj.problem.pojo.dto.ProblemCodeTemplateListDTO;
import com.glowxq.oj.problem.pojo.dto.ProblemCodeTemplateUpdateDTO;
import com.glowxq.oj.problem.pojo.po.ProblemCodeTemplate;
import com.glowxq.oj.problem.pojo.vo.ProblemCodeTemplateVO;
import com.glowxq.oj.problem.service.ProblemCodeTemplateService;
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
 * 题目代码模版 服务实现类
 * </p>
 *
 * @author glowxq
 * @since 2025-03-18
 */
@Service
@RequiredArgsConstructor
public class ProblemCodeTemplateServiceImpl extends ServiceImpl<ProblemCodeTemplateMapper, ProblemCodeTemplate> implements ProblemCodeTemplateService {

    private static QueryWrapper buildQueryWrapper(ProblemCodeTemplateListDTO dto) {
        QueryWrapper wrapper = QueryWrapper.create().from(ProblemCodeTemplate.class);
        wrapper.eq(ProblemCodeTemplate::getProblemId, dto.getProblemId(), Utils.isNotNull(dto.getProblemId()));
        wrapper.eq(ProblemCodeTemplate::getLanguageId, dto.getLanguageId(), Utils.isNotNull(dto.getLanguageId()));
        wrapper.eq(ProblemCodeTemplate::getCode, dto.getCode(), Utils.isNotNull(dto.getCode()));
        wrapper.eq(ProblemCodeTemplate::getEnable, dto.getEnable(), Utils.isNotNull(dto.getEnable()));
        return wrapper;
    }

    @Override
    public void create(ProblemCodeTemplateCreateDTO dto) {
        ProblemCodeTemplate problemCodeTemplate = BeanCopyUtils.copy(dto, ProblemCodeTemplate.class);
        save(problemCodeTemplate);
    }

    @Override
    public void update(ProblemCodeTemplateUpdateDTO dto) {
        ProblemCodeTemplate problemCodeTemplate = BeanCopyUtils.copy(dto, ProblemCodeTemplate.class);
        QueryWrapper wrapper;
        // id有效性校验
        wrapper = QueryWrapper.create()
                              .eq(ProblemCodeTemplate::getId, dto.getId());
        CommonResponseEnum.INVALID_ID.assertTrue(count(wrapper) <= 0);

        saveOrUpdate(problemCodeTemplate);
    }

    @Override
    public PageResult<ProblemCodeTemplateVO> page(ProblemCodeTemplateListDTO dto) {
        Page<ProblemCodeTemplateVO> page = pageAs(PageUtils.getPage(dto), buildQueryWrapper(dto), ProblemCodeTemplateVO.class);
        return PageUtils.getPageResult(page);
    }

    @Override
    public List<ProblemCodeTemplateVO> list(ProblemCodeTemplateListDTO dto) {
        return listAs(buildQueryWrapper(dto), ProblemCodeTemplateVO.class);
    }

    @Override
    public void remove(SelectIdsDTO dto) {
        CommonResponseEnum.INVALID_ID.assertTrue(dto.getIds().isEmpty());
        removeByIds(dto.getIds());
    }

    @Override
    public ProblemCodeTemplateVO detail(Object id) {
        ProblemCodeTemplate problemCodeTemplate = getById((Serializable) id);
        CommonResponseEnum.INVALID_ID.assertNull(problemCodeTemplate);
        return BeanCopyUtils.copy(problemCodeTemplate, ProblemCodeTemplateVO.class);
    }

    @SneakyThrows
    @Override
    public void importExcel(ImportExcelDTO dto) {
        ExcelResult<ProblemCodeTemplateImportDTO> excelResult = ExcelUtils.importExcel(dto.getFile().getInputStream(), ProblemCodeTemplateImportDTO.class, true);
        List<ProblemCodeTemplateImportDTO> list = excelResult.getList();
        List<String> errorList = excelResult.getErrorList();
        String analysis = excelResult.getAnalysis();
        System.out.println(" analysis : " + analysis);
        System.out.println(" isCover : " + dto.getIsCover());
    }

    @SneakyThrows
    @Override
    public void exportExcel(ProblemCodeTemplateListDTO dto, HttpServletResponse response) {
        List<ProblemCodeTemplateVO> list = list(dto);
        String fileName = "题目代码模版模板";
        OutputStream os = FileUtils.getOutputStream(response, fileName + ".xlsx");
        ExcelUtils.exportExcel(list, "题目代码模版", ProblemCodeTemplateVO.class, os);
    }

    @Override
    public void bindCodeTemplateIds(Long problemId, List<ProblemCodeTemplateCreateDTO> codeTemplates) {
        if (CollectionUtils.isEmpty(codeTemplates) || problemId == null) {
            return;
        }
        List<ProblemCodeTemplate> problemCodeTemplates = codeTemplates.stream().map(codeTemplate -> {
            ProblemCodeTemplate problemCodeTemplate = codeTemplate.buildEntity();
            problemCodeTemplate.setProblemId(problemId);
            problemCodeTemplate.setLanguageId(codeTemplate.getLanguageId());
            problemCodeTemplate.setEnable(true);
            return problemCodeTemplate;
        }).toList();
        mapper.insertBatch(problemCodeTemplates);
    }

    @Override
    public void deleteByProblemId(Long problemId) {
        mapper.deleteByProblemId(problemId);
    }

    @Override
    public List<ProblemCodeTemplate> listByProblemId(Long problemId) {
        return mapper.listByProblemId(problemId);
    }
}