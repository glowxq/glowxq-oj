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
import com.glowxq.oj.problem.mapper.ProblemBlackMapper;
import com.glowxq.oj.problem.pojo.dto.ProblemBlackCreateDTO;
import com.glowxq.oj.problem.pojo.dto.ProblemBlackImportDTO;
import com.glowxq.oj.problem.pojo.dto.ProblemBlackListDTO;
import com.glowxq.oj.problem.pojo.dto.ProblemBlackUpdateDTO;
import com.glowxq.oj.problem.pojo.po.ProblemBlack;
import com.glowxq.oj.problem.pojo.vo.ProblemBlackVO;
import com.glowxq.oj.problem.service.ProblemBlackService;
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
 * 题目黑名单 服务实现类
 * </p>
 *
 * @author glowxq
 * @since 2025-04-11
 */
@Service
@RequiredArgsConstructor
public class ProblemBlackServiceImpl extends ServiceImpl<ProblemBlackMapper, ProblemBlack> implements ProblemBlackService {
    @Override
    public void create(ProblemBlackCreateDTO dto){
        ProblemBlack problemBlack = BeanCopyUtils.copy(dto, ProblemBlack.class);
        save(problemBlack);
    }

    @Override
    public void update(ProblemBlackUpdateDTO dto){
        ProblemBlack problemBlack = BeanCopyUtils.copy(dto, ProblemBlack.class);
        QueryWrapper wrapper;
        // id有效性校验
        wrapper = QueryWrapper.create()
            .eq(ProblemBlack::getId, dto.getId());
        CommonResponseEnum.INVALID_ID.assertTrue(count(wrapper) <= 0);

        saveOrUpdate(problemBlack);
    }

    @Override
    public PageResult<ProblemBlackVO> page(ProblemBlackListDTO dto){
        Page<ProblemBlackVO> page = pageAs(PageUtils.getPage(dto), buildQueryWrapper(dto), ProblemBlackVO.class);
        return PageUtils.getPageResult(page);
    }

    @Override
    public List<ProblemBlackVO> list(ProblemBlackListDTO dto){
        return listAs(buildQueryWrapper(dto), ProblemBlackVO.class);
    }

    @Override
    public void remove(SelectIdsDTO dto){
        CommonResponseEnum.INVALID_ID.assertTrue(dto.getIds().isEmpty());
        removeByIds(dto.getIds());
    }

    @Override
    public ProblemBlackVO detail(Object id){
        ProblemBlack problemBlack = getById((Serializable) id);
        CommonResponseEnum.INVALID_ID.assertNull(problemBlack);
        return BeanCopyUtils.copy(problemBlack, ProblemBlackVO.class);
    }

    @SneakyThrows
    @Override
    public void importExcel(ImportExcelDTO dto) {
        ExcelResult<ProblemBlackImportDTO> excelResult = ExcelUtils.importExcel(dto.getFile().getInputStream(), ProblemBlackImportDTO.class, true);
        List<ProblemBlackImportDTO> list = excelResult.getList();
        List<String> errorList = excelResult.getErrorList();
        String analysis = excelResult.getAnalysis();
        System.out.println(" analysis : " + analysis);
        System.out.println(" isCover : " + dto.getIsCover());
    }

    @SneakyThrows
    @Override
    public void exportExcel(ProblemBlackListDTO dto, HttpServletResponse response) {
        List<ProblemBlackVO> list = list(dto);
        String fileName = "题目黑名单模板";
        OutputStream os = FileUtils.getOutputStream(response, fileName + ".xlsx");
        ExcelUtils.exportExcel(list, "题目黑名单", ProblemBlackVO.class, os);
    }

    private static QueryWrapper buildQueryWrapper(ProblemBlackListDTO dto) {
    QueryWrapper wrapper = QueryWrapper.create().from(ProblemBlack.class);
            wrapper.eq(ProblemBlack::getProblemId, dto.getProblemId(), Utils.isNotNull(dto.getProblemId()));
            wrapper.eq(ProblemBlack::getBusinessId, dto.getBusinessId(), Utils.isNotNull(dto.getBusinessId()));
            wrapper.like(ProblemBlack::getBusinessName, dto.getBusinessName(), Utils.isNotNull(dto.getBusinessName()));
            wrapper.eq(ProblemBlack::getType, dto.getType(), Utils.isNotNull(dto.getType()));
    return wrapper;
    }
}