package com.glowxq.oj.problem.handler.create;

import com.glowxq.core.common.exception.common.BusinessException;
import com.glowxq.oj.problem.pojo.dto.ProblemCaseBO;
import com.glowxq.oj.problem.pojo.dto.ProblemCreateUpdateDTO;
import com.glowxq.oj.problem.pojo.po.Problem;
import com.glowxq.oj.problem.service.ProblemCodeTemplateService;
import com.glowxq.oj.problem.service.ProblemLanguageService;
import com.glowxq.oj.problem.utils.ProblemUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author glowxq
 * @version 1.0
 * @date 2025/3/18
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ProgramProblemCreateHandler extends BaseProblemCreateHandler {

    private final ProblemLanguageService problemLanguageService;

    private final ProblemCodeTemplateService problemCodeTemplateService;

    @Override
    public void doCreateProblemBusinessData(ProblemCreateUpdateDTO dto, Problem problem) {
        // 保存语言
        problemLanguageService.bindLanguageIds(problem.getId(), dto.getLanguageIds());
        // 为新的题目添加对应的codeTemplate
        problemCodeTemplateService.bindCodeTemplateIds(problem.getId(), dto.getCodeTemplates());
        // 保存题目测试样例
        problemService.bindProblemCaseBatch(problem, dto.getProblemCaseDataList(), dto.problemCaseType(), dto.getUploadTestcaseDir(), dto.getAutoDeleteUploadTestcaseDir());
    }

    @Override
    protected void beforeCheck(ProblemCreateUpdateDTO dto) {
        List<ProblemCaseBO> samples = dto.getProblemCaseDataList();
        if (CollectionUtils.isEmpty(samples)) {
            throw new BusinessException("编程题-题目测试样例不能为空");
        }
    }

    @Override
    protected void preCreate(ProblemCreateUpdateDTO dto) {
        super.preCreate(dto);
        ProblemUtils.resetProblemInfo(dto);
    }
}
