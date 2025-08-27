package com.glowxq.oj.problem.service;

import com.glowxq.core.common.entity.ImportExcelDTO;
import com.glowxq.core.common.entity.PageResult;
import com.glowxq.core.common.entity.SelectIdsDTO;
import com.glowxq.oj.problem.enums.ProblemCaseType;
import com.glowxq.oj.problem.pojo.dto.ProblemCaseBO;
import com.glowxq.oj.problem.pojo.dto.ProblemCreateUpdateDTO;
import com.glowxq.oj.problem.pojo.dto.ProblemListDTO;
import com.glowxq.oj.problem.pojo.dto.ProblemUpdateDTO;
import com.glowxq.oj.problem.pojo.po.Problem;
import com.glowxq.oj.problem.pojo.vo.ProblemVO;
import com.mybatisflex.core.service.IService;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

/**
 * <p>
 * 题目 Service
 * </p>
 *
 * @author glowxq
 * @since 2025-03-17
 */
public interface ProblemService extends IService<Problem> {

    void create(ProblemCreateUpdateDTO dto);

    void update(ProblemUpdateDTO dto);

    PageResult<ProblemVO> page(ProblemListDTO dto);

    List<ProblemVO> list(ProblemListDTO dto);

    void remove(SelectIdsDTO dto);

    ProblemCreateUpdateDTO detail(Long problemId) ;

    void importExcel(ImportExcelDTO dto);

    void exportExcel(ProblemListDTO dto, HttpServletResponse response);

    void bindProblemCaseBatch(Problem problem, List<ProblemCaseBO> problemCaseDataList, ProblemCaseType problemCaseType, String uploadTestcaseDir, Boolean autoDeleteUploadTestcaseDir);

}