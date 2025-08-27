package com.glowxq.oj.judge.service.impl;

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
import com.glowxq.oj.judge.mapper.JudgeCaseMapper;
import com.glowxq.oj.judge.pojo.dto.JudgeCaseCreateDTO;
import com.glowxq.oj.judge.pojo.dto.JudgeCaseImportDTO;
import com.glowxq.oj.judge.pojo.dto.JudgeCaseListDTO;
import com.glowxq.oj.judge.pojo.dto.JudgeCaseUpdateDTO;
import com.glowxq.oj.judge.pojo.po.JudgeCase;
import com.glowxq.oj.judge.pojo.vo.JudgeCaseVO;
import com.glowxq.oj.judge.service.JudgeCaseService;
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
 * 测评用例情况 服务实现类
 * </p>
 *
 * @author glowxq
 * @since 2025-03-17
 */
@Service
@RequiredArgsConstructor
public class JudgeCaseServiceImpl extends ServiceImpl<JudgeCaseMapper, JudgeCase> implements JudgeCaseService {
    @Override
    public void create(JudgeCaseCreateDTO dto){
        JudgeCase judgeCase = BeanCopyUtils.copy(dto, JudgeCase.class);
        save(judgeCase);
    }

    @Override
    public void update(JudgeCaseUpdateDTO dto){
        JudgeCase judgeCase = BeanCopyUtils.copy(dto, JudgeCase.class);
        QueryWrapper wrapper;
        // id有效性校验
        wrapper = QueryWrapper.create()
            .eq(JudgeCase::getId, dto.getId());
        CommonResponseEnum.INVALID_ID.assertTrue(count(wrapper) <= 0);

        saveOrUpdate(judgeCase);
    }

    @Override
    public PageResult<JudgeCaseVO> page(JudgeCaseListDTO dto){
        Page<JudgeCaseVO> page = pageAs(PageUtils.getPage(dto), buildQueryWrapper(dto), JudgeCaseVO.class);
        return PageUtils.getPageResult(page);
    }

    @Override
    public List<JudgeCaseVO> list(JudgeCaseListDTO dto){
        return listAs(buildQueryWrapper(dto), JudgeCaseVO.class);
    }

    @Override
    public void remove(SelectIdsDTO dto){
        CommonResponseEnum.INVALID_ID.assertTrue(dto.getIds().isEmpty());
        removeByIds(dto.getIds());
    }

    @Override
    public JudgeCaseVO detail(Object id){
        JudgeCase judgeCase = getById((Serializable) id);
        CommonResponseEnum.INVALID_ID.assertNull(judgeCase);
        return BeanCopyUtils.copy(judgeCase, JudgeCaseVO.class);
    }

    @SneakyThrows
    @Override
    public void importExcel(ImportExcelDTO dto) {
        ExcelResult<JudgeCaseImportDTO> excelResult = ExcelUtils.importExcel(dto.getFile().getInputStream(), JudgeCaseImportDTO.class, true);
        List<JudgeCaseImportDTO> list = excelResult.getList();
        List<String> errorList = excelResult.getErrorList();
        String analysis = excelResult.getAnalysis();
        System.out.println(" analysis : " + analysis);
        System.out.println(" isCover : " + dto.getIsCover());
    }

    @SneakyThrows
    @Override
    public void exportExcel(JudgeCaseListDTO dto, HttpServletResponse response) {
        List<JudgeCaseVO> list = list(dto);
        String fileName = "测评用例情况模板";
        OutputStream os = FileUtils.getOutputStream(response, fileName + ".xlsx");
        ExcelUtils.exportExcel(list, "测评用例情况", JudgeCaseVO.class, os);
    }

    @Override
    public List<JudgeCase> listByJudgeId(Long id) {
        return mapper.listByJudgeId(id);
    }

    private static QueryWrapper buildQueryWrapper(JudgeCaseListDTO dto) {
        QueryWrapper wrapper = QueryWrapper.create().from(JudgeCase.class);
        wrapper.eq(JudgeCase::getJudgeId, dto.getJudgeId(), Utils.isNotNull(dto.getJudgeId()));
        wrapper.eq(JudgeCase::getJudgeKey, dto.getJudgeKey(), Utils.isNotNull(dto.getJudgeKey()));
        wrapper.eq(JudgeCase::getUserId, dto.getUserId(), Utils.isNotNull(dto.getUserId()));
        wrapper.eq(JudgeCase::getProblemId, dto.getProblemId(), Utils.isNotNull(dto.getProblemId()));
        wrapper.eq(JudgeCase::getProblemKey, dto.getProblemKey(), Utils.isNotNull(dto.getProblemKey()));
        wrapper.eq(JudgeCase::getCaseId, dto.getCaseId(), Utils.isNotNull(dto.getCaseId()));
        wrapper.eq(JudgeCase::getStatus, dto.getStatus(), Utils.isNotNull(dto.getStatus()));
        wrapper.eq(JudgeCase::getTime, dto.getTime(), Utils.isNotNull(dto.getTime()));
        wrapper.eq(JudgeCase::getMemory, dto.getMemory(), Utils.isNotNull(dto.getMemory()));
        wrapper.eq(JudgeCase::getScore, dto.getScore(), Utils.isNotNull(dto.getScore()));
        wrapper.eq(JudgeCase::getGroupNum, dto.getGroupNum(), Utils.isNotNull(dto.getGroupNum()));
        wrapper.eq(JudgeCase::getSeq, dto.getSeq(), Utils.isNotNull(dto.getSeq()));
        wrapper.eq(JudgeCase::getMode, dto.getMode(), Utils.isNotNull(dto.getMode()));
        wrapper.eq(JudgeCase::getInputData, dto.getInputData(), Utils.isNotNull(dto.getInputData()));
        wrapper.eq(JudgeCase::getOutputData, dto.getOutputData(), Utils.isNotNull(dto.getOutputData()));
        wrapper.eq(JudgeCase::getUserOutput, dto.getUserOutput(), Utils.isNotNull(dto.getUserOutput()));
        return wrapper;
    }
}