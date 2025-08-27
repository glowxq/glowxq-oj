package com.glowxq.oj.user.mapper;

import com.glowxq.oj.judge.enums.JudgeStatus;
import com.glowxq.oj.user.pojo.po.UserProblem;
import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.query.QueryWrapper;

/**
* <p>
* 用户题目数据 Mapper 接口
* </p>
*
* @author glowxq
* @since 2025-04-03
*/
public interface UserProblemMapper extends BaseMapper<UserProblem> {

    default UserProblem getByUserAndProblem(Long userId, Long problemId, JudgeStatus status){
        QueryWrapper queryWrapper = QueryWrapper.create();
        queryWrapper.from(UserProblem.class);
        queryWrapper.eq(UserProblem::getUserId, userId);
        queryWrapper.eq(UserProblem::getProblemId, problemId);
        queryWrapper.eq(UserProblem::getJudgeStatus, status.getCode());
        return this.selectOneByQuery(queryWrapper);
    }
}