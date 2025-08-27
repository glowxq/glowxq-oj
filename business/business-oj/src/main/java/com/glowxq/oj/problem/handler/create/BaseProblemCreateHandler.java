package com.glowxq.oj.problem.handler.create;

import cn.idev.excel.util.StringUtils;
import com.glowxq.core.common.exception.common.AlertsException;
import com.glowxq.core.common.exception.common.BusinessException;
import com.glowxq.oj.common.enums.OjTagBind;
import com.glowxq.oj.problem.enums.ProblemType;
import com.glowxq.oj.problem.mapper.ProblemMapper;
import com.glowxq.oj.problem.pojo.dto.ProblemBO;
import com.glowxq.oj.problem.pojo.dto.ProblemCreateUpdateDTO;
import com.glowxq.oj.problem.pojo.po.Problem;
import com.glowxq.oj.problem.service.ProblemService;
import com.glowxq.system.meta.service.MetaTagService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author glowxq
 * @version 1.0
 * @date 2025/3/18
 */
@Slf4j
public abstract class BaseProblemCreateHandler implements ProblemCreateInterface {

    @Autowired
    protected ProblemService problemService;

    @Autowired
    protected MetaTagService metaTagService;

    @Autowired
    protected ProblemMapper problemMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(ProblemCreateUpdateDTO dto) {
        log.info("开始创建题目:{}", dto);
        beforeCheck(dto);
        log.info("beforeCheck 检测题目数据完成:{}", dto);
        preCreate(dto);
        log.info("preCreate 预格式化数据完成:{}", dto);
        Problem problem = baseCreate(dto);
        log.info("baseCreate 创建题目基本数据完成:{}", dto);
        doCreateProblemBusinessData(dto, problem);
        log.info("doCreateProblemBusinessData 创建题目业务数据完成:{}", dto);
    }

    /**
     * 创建题目业务数据
     *
     * @param dto
     * @param problem
     */
    protected abstract void doCreateProblemBusinessData(ProblemCreateUpdateDTO dto, Problem problem);

    /**
     * 在创建问题之前进行的检查操作
     * 此方法旨在确保问题的唯一性，通过检查问题键值是否已存在来实现
     *
     * @param dto 包含问题详细信息的DTO，用于创建问题
     * @throws AlertsException 如果检测到问题键值已存在，则抛出异常，提示题目编号已存在
     */
    protected void beforeCheck(ProblemCreateUpdateDTO dto) {
        // 获取问题的唯一键值
        String problemKey = dto.getProblemBo().getProblemKey();
        // 检查问题键值是否不为空且不只包含空白字符
        if (StringUtils.isNotBlank(problemKey)) {
            // 尝试通过问题键值从数据库中获取问题实例
            Problem problem = problemMapper.getByProblemKey(problemKey);
            // 如果找到匹配的问题实例，则抛出异常，表明题目编号已存在
            if (problem != null) {
                throw new BusinessException("题目编号已存在");
            }
        }
    }

    /**
     * 编程题创建前的预处理方法
     * 该方法主要用于处理编程题创建前的一些准备工作，如自动生成题目编号等
     *
     * @param dto 编程题创建的DTO，包含创建编程题所需的信息
     */
    protected void preCreate(ProblemCreateUpdateDTO dto) {
        log.info("preCreate 创建题:{}", dto);

        // 获取编程题的基本信息DTO
        ProblemBO problemDto = dto.getProblemBo();
        // 获取题目编号
        String problemKey = problemDto.getProblemKey();

        // 如果题目编号为空，则自动生成
        if (StringUtils.isBlank(problemKey)) {
            // 获取编程题类型
            ProblemType problemType = dto.problemType();
            // 根据编程题类型查询该类型题目已有的数量
            Long problemTypeCount = problemMapper.countByProblemType(dto.problemType());
            // 数量加一，用于生成新的题目编号
            problemTypeCount++;
            // 拼接题目编号，并设置到DTO中
            problemDto.setProblemKey(problemType.getSerialPrefix() + problemTypeCount);
        }

        // 将更新后的编程题基本信息DTO设置回原始DTO中
        dto.setProblemBo(problemDto);
    }

    /**
     * 基础创建编程问题
     * 该方法主要用于将ProblemCreateDTO中包含的信息转换为Problem实体，并进行保存
     * 包括绑定相关的标签
     *
     * @param dto 包含要创建的编程问题信息的DTO
     * @return 返回创建后的Problem实体
     */
    protected Problem baseCreate(ProblemCreateUpdateDTO dto) {
        // 日志记录创建编程题的信息
        log.info("创建编程题:{}", dto);

        // 保存题目主表信息
        Problem problem = dto.getProblemBo().buildEntity();
        // 设置问题类型
        problem.setProblemType(dto.problemType().getCode());
        // 插入问题到数据库，开启自动填充
        problemMapper.insert(problem, true);

        // 绑定标签
        metaTagService.bindTags(problem.getId(), dto.getTagIds(), OjTagBind.Problem);
        return problem;
    }
}
