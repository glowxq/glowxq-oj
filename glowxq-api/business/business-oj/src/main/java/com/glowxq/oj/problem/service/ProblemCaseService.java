package com.glowxq.oj.problem.service;

import com.glowxq.core.common.entity.ImportExcelDTO;
import com.glowxq.core.common.entity.PageResult;
import com.glowxq.core.common.entity.SelectIdsDTO;
import com.glowxq.oj.problem.enums.ProblemCaseType;
import com.glowxq.oj.problem.pojo.dto.ProblemCaseBO;
import com.glowxq.oj.problem.pojo.dto.ProblemCaseListDTO;
import com.glowxq.oj.problem.pojo.dto.ProblemCaseUpdateDTO;
import com.glowxq.oj.problem.pojo.po.Problem;
import com.glowxq.oj.problem.pojo.po.ProblemCase;
import com.glowxq.oj.problem.pojo.vo.ProblemCaseVO;
import com.mybatisflex.core.service.IService;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

/**
 * <p>
 * 测试用例 Service
 * </p>
 *
 * @author glowxq
 * @since 2025-03-17
 */
public interface ProblemCaseService extends IService<ProblemCase> {

    void create(ProblemCaseBO dto);

    void update(ProblemCaseUpdateDTO dto);

    PageResult<ProblemCaseVO> page(ProblemCaseListDTO dto);

    List<ProblemCaseVO> list(ProblemCaseListDTO dto);

    void remove(SelectIdsDTO dto);

    ProblemCaseVO detail(Object id);

    void importExcel(ImportExcelDTO dto);

    void exportExcel(ProblemCaseListDTO dto, HttpServletResponse response);

    List<ProblemCase> listByProblemId(Long problemId);

    List<ProblemCase> saveCaseBatch(Problem problem, List<ProblemCaseBO> problemCaseDataList, ProblemCaseType problemCaseType, String uploadTestcaseDir, Boolean autoDeleteUploadTestcaseDir);
}