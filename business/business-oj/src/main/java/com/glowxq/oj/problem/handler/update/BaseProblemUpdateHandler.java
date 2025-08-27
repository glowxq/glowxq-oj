package com.glowxq.oj.problem.handler.update;

import com.glowxq.core.common.exception.common.BusinessException;
import com.glowxq.oj.common.enums.OjTagBind;
import com.glowxq.oj.problem.mapper.ProblemMapper;
import com.glowxq.oj.problem.pojo.dto.ProblemCreateUpdateDTO;
import com.glowxq.oj.problem.pojo.po.Problem;
import com.glowxq.oj.problem.service.ProblemCodeTemplateService;
import com.glowxq.oj.problem.service.ProblemLanguageService;
import com.glowxq.oj.problem.service.ProblemOptionService;
import com.glowxq.oj.problem.service.ProblemService;
import com.glowxq.system.meta.service.MetaTagService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author glowxq
 * @version 1.0
 * @date 2025/3/18
 */
@Slf4j
@Component
public abstract class BaseProblemUpdateHandler implements ProblemUpdateInterface {

    @Autowired
    protected ProblemMapper problemMapper;

    @Autowired
    protected ProblemService problemService;

    @Autowired
    protected MetaTagService metaTagService;

    @Autowired
    protected ProblemOptionService problemOptionService;

    @Autowired
    protected ProblemLanguageService problemLanguageService;

    @Autowired
    protected ProblemCodeTemplateService problemCodeTemplateService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(ProblemCreateUpdateDTO dto) {
        this.checkDTO(dto);
        this.updateBaseProblem(dto);
        this.removeProblemData(dto);
        this.saveBaseProblemData(dto);
        this.doCreateProblemData(dto);
    }

    /**
     * 保存基本题目数据（使用通用方法）
     *
     * @param dto 题目创建更新DTO
     */
    protected void saveBaseProblemData(ProblemCreateUpdateDTO dto) {
        saveCommonProblemData(dto);
    }

    protected abstract void doCreateProblemData(ProblemCreateUpdateDTO dto);

    /**
     * 更新题目基本信息
     *
     * @param dto
     */
    protected void updateBaseProblem(ProblemCreateUpdateDTO dto) {
        Problem problem = dto.getProblemBo().buildEntity();
        problemService.updateById(problem);
    }

    /**
     * 删除题目数据（使用通用方法）
     *
     * @param dto 题目创建更新DTO
     */
    protected void removeProblemData(ProblemCreateUpdateDTO dto) {
        Long problemId = dto.getProblemBo().getId();
        cleanupProblemData(problemId);
    }

    /**
     * 校验dto
     *
     * @param dto 题目创建更新DTO
     */
    protected void checkDTO(ProblemCreateUpdateDTO dto) {
        if (dto == null) {
            throw new BusinessException("题目数据不能为空");
        }
        if (dto.getProblemBo() == null) {
            throw new BusinessException("题目基本信息不能为空");
        }
        if (dto.getProblemBo().getId() == null) {
            throw new BusinessException("题目ID不能为空");
        }
    }

    /**
     * 通用的题目数据清理方法
     *
     * @param problemId 题目ID
     */
    protected void cleanupProblemData(Long problemId) {
        // 删除标签
        metaTagService.deleteByBusinessId(problemId, OjTagBind.Problem);
        // 删除语言
        problemLanguageService.deleteByProblemId(problemId);
        // 删除题目对应的codeTemplate
        problemCodeTemplateService.deleteByProblemId(problemId);
        // 删除题目选项
        problemOptionService.deleteByProblemId(problemId);
    }

    /**
     * 通用的题目基础数据保存方法
     *
     * @param dto 题目创建更新DTO
     */
    protected void saveCommonProblemData(ProblemCreateUpdateDTO dto) {
        List<Long> tagIds = dto.getTagIds();
        if (tagIds != null && !tagIds.isEmpty()) {
            metaTagService.bindTags(dto.getProblemBo().getId(), tagIds, OjTagBind.Problem);
        }
    }
}
