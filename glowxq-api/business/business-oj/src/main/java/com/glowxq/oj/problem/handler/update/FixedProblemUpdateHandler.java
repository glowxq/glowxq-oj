package com.glowxq.oj.problem.handler.update;

import com.glowxq.oj.problem.pojo.dto.ProblemCreateUpdateDTO;
import com.glowxq.oj.problem.pojo.po.Problem;
import com.glowxq.oj.problem.pojo.po.ProblemOption;
import com.glowxq.oj.problem.utils.ProblemUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author glowxq
 * @version 1.0
 * @date 2025/3/20
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FixedProblemUpdateHandler extends BaseProblemUpdateHandler {

    @Override
    protected void doCreateProblemData(ProblemCreateUpdateDTO dto) {
        ProblemUtils.resetProblemInfo(dto);
        Long problemId = dto.getProblemBo().getId();
        Problem problem = problemService.getById(problemId);
        List<ProblemOption> problemOptionList = ProblemUtils.buildProblemOptions(dto, problem);
        problemOptionService.saveBatch(problemOptionList);
    }
}
