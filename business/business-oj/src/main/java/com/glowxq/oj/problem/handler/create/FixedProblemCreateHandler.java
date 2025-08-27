package com.glowxq.oj.problem.handler.create;

import com.glowxq.core.common.exception.common.BusinessException;
import com.glowxq.oj.problem.pojo.dto.ProblemCreateUpdateDTO;
import com.glowxq.oj.problem.pojo.po.Problem;
import com.glowxq.oj.problem.pojo.po.ProblemOption;
import com.glowxq.oj.problem.service.ProblemOptionService;
import com.glowxq.oj.problem.utils.ProblemUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author glowxq
 * @version 1.0
 * @date 2025/3/18
 */
@Component
@RequiredArgsConstructor
public class FixedProblemCreateHandler extends BaseProblemCreateHandler {

    private final ProblemOptionService problemOptionService;

    @Override
    protected void doCreateProblemBusinessData(ProblemCreateUpdateDTO dto, Problem problem) {
        List<ProblemOption> problemOptionList = ProblemUtils.buildProblemOptions(dto, problem);
        problemOptionService.saveBatch(problemOptionList);
    }

    @Override
    protected void beforeCheck(ProblemCreateUpdateDTO dto) {
        super.beforeCheck(dto);
        if (CollectionUtils.isEmpty(dto.getOptions())) {
            throw new BusinessException("缺少选项数据");
        }
    }
}
