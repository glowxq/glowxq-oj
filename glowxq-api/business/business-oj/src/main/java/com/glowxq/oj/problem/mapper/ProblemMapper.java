package com.glowxq.oj.problem.mapper;

import com.glowxq.oj.problem.enums.ProblemType;
import com.glowxq.oj.problem.pojo.po.Problem;
import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.query.QueryWrapper;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

/**
* <p>
* 题目 Mapper 接口
* </p>
*
* @author glowxq
* @since 2025-03-17
*/
public interface ProblemMapper extends BaseMapper<Problem> {

    default Problem getByProblemKey(String problemKey) {
        QueryWrapper qw = QueryWrapper.create().from(Problem.class).eq(Problem::getProblemKey, problemKey);
        return this.selectOneByQuery(qw);
    }

   default Long countByProblemType(ProblemType problemType){
        QueryWrapper qw = QueryWrapper.create().from(Problem.class).eq(Problem::getProblemType, problemType.getCode());
        return this.selectCountByQuery(qw);
   }

    default List<Problem> listByIds(List<Long> problemIds){
        if (CollectionUtils.isEmpty(problemIds)){
            return List.of();
        }
        return this.selectListByIds(problemIds);
    }
}