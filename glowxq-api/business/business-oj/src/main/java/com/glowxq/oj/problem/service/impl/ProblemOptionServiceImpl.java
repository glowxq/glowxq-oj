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
import com.glowxq.oj.problem.mapper.ProblemOptionMapper;
import com.glowxq.oj.problem.pojo.dto.ProblemOptionBO;
import com.glowxq.oj.problem.pojo.dto.ProblemOptionImportDTO;
import com.glowxq.oj.problem.pojo.dto.ProblemOptionListDTO;
import com.glowxq.oj.problem.pojo.dto.ProblemOptionUpdateDTO;
import com.glowxq.oj.problem.pojo.po.ProblemOption;
import com.glowxq.oj.problem.pojo.vo.ProblemOptionVO;
import com.glowxq.oj.problem.service.ProblemOptionService;
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
 * 题目选项 服务实现类
 * </p>
 *
 * @author glowxq
 * @since 2025-03-17
 */
@Service
@RequiredArgsConstructor
public class ProblemOptionServiceImpl extends ServiceImpl<ProblemOptionMapper, ProblemOption> implements ProblemOptionService {
    @Override
    public void create(ProblemOptionBO dto){
        ProblemOption problemOption = BeanCopyUtils.copy(dto, ProblemOption.class);
        save(problemOption);
    }

    @Override
    public void update(ProblemOptionUpdateDTO dto){
        ProblemOption problemOption = BeanCopyUtils.copy(dto, ProblemOption.class);
        QueryWrapper wrapper;
        // id有效性校验
        wrapper = QueryWrapper.create()
            .eq(ProblemOption::getId, dto.getId());
        CommonResponseEnum.INVALID_ID.assertTrue(count(wrapper) <= 0);

        saveOrUpdate(problemOption);
    }

    @Override
    public PageResult<ProblemOptionVO> page(ProblemOptionListDTO dto){
        Page<ProblemOptionVO> page = pageAs(PageUtils.getPage(dto), buildQueryWrapper(dto), ProblemOptionVO.class);
        return PageUtils.getPageResult(page);
    }

    @Override
    public List<ProblemOptionVO> list(ProblemOptionListDTO dto){
        return listAs(buildQueryWrapper(dto), ProblemOptionVO.class);
    }

    @Override
    public void remove(SelectIdsDTO dto){
        CommonResponseEnum.INVALID_ID.assertTrue(dto.getIds().isEmpty());
        removeByIds(dto.getIds());
    }

    @Override
    public ProblemOptionVO detail(Object id){
        ProblemOption problemOption = getById((Serializable) id);
        CommonResponseEnum.INVALID_ID.assertNull(problemOption);
        return BeanCopyUtils.copy(problemOption, ProblemOptionVO.class);
    }

    @SneakyThrows
    @Override
    public void importExcel(ImportExcelDTO dto) {
        ExcelResult<ProblemOptionImportDTO> excelResult = ExcelUtils.importExcel(dto.getFile().getInputStream(), ProblemOptionImportDTO.class, true);
        List<ProblemOptionImportDTO> list = excelResult.getList();
        List<String> errorList = excelResult.getErrorList();
        String analysis = excelResult.getAnalysis();
        System.out.println(" analysis : " + analysis);
        System.out.println(" isCover : " + dto.getIsCover());
    }

    @SneakyThrows
    @Override
    public void exportExcel(ProblemOptionListDTO dto, HttpServletResponse response) {
        List<ProblemOptionVO> list = list(dto);
        String fileName = "题目选项模板";
        OutputStream os = FileUtils.getOutputStream(response, fileName + ".xlsx");
        ExcelUtils.exportExcel(list, "题目选项", ProblemOptionVO.class, os);
    }

    @Override
    public List<ProblemOption> listProblemId(Long problemId) {
        return mapper.listProblemId(problemId);
    }

    @Override
    public void deleteByProblemId(Long problemId) {
        mapper.deleteByProblemId(problemId);
    }

    private static QueryWrapper buildQueryWrapper(ProblemOptionListDTO dto) {
        QueryWrapper wrapper = QueryWrapper.create().from(ProblemOption.class);
        wrapper.eq(ProblemOption::getProblemId, dto.getProblemId(), Utils.isNotNull(dto.getProblemId()));
        wrapper.eq(ProblemOption::getProblemKey, dto.getProblemKey(), Utils.isNotNull(dto.getProblemKey()));
        wrapper.eq(ProblemOption::getOptionKey, dto.getOptionKey(), Utils.isNotNull(dto.getOptionKey()));
        wrapper.eq(ProblemOption::getProblemType, dto.getProblemType(), Utils.isNotNull(dto.getProblemType()));
        wrapper.eq(ProblemOption::getScore, dto.getScore(), Utils.isNotNull(dto.getScore()));
        wrapper.eq(ProblemOption::getOptionContent, dto.getOptionContent(), Utils.isNotNull(dto.getOptionContent()));
        wrapper.eq(ProblemOption::getAnswer, dto.getAnswer(), Utils.isNotNull(dto.getAnswer()));
        return wrapper;
    }
}