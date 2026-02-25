package com.glowxq.oj.problem.handler.update;

import com.glowxq.oj.problem.pojo.dto.ProblemCreateUpdateDTO;
import com.glowxq.oj.problem.pojo.po.Problem;
import com.glowxq.oj.problem.utils.ProblemUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author glowxq
 * @version 1.0
 * @date 2025/3/20
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ProgramProblemUpdateHandler extends BaseProblemUpdateHandler {

    @Override
    protected void doCreateProblemData(ProblemCreateUpdateDTO dto) {
        ProblemUtils.resetProblemInfo(dto);
        Long problemId = dto.getProblemBo().getId();
        Problem problem = problemService.getById(problemId);
        // 保存语言
        problemLanguageService.bindLanguageIds(problem.getId(), dto.getLanguageIds());
        // 为新的题目添加对应的codeTemplate
        problemCodeTemplateService.bindCodeTemplateIds(problem.getId(), dto.getCodeTemplates());
    }
}
