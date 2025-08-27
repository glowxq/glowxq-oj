package com.glowxq.oj.judge.mapper;

import com.glowxq.oj.judge.enums.JudgeSceneType;
import com.glowxq.oj.judge.enums.JudgeStatus;
import com.glowxq.oj.judge.pojo.po.Judge;
import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.query.QueryWrapper;

/**
 * <p>
 * 测评记录 Mapper 接口
 * </p>
 *
 * @author glowxq
 * @since 2025-03-17
 */
public interface JudgeMapper extends BaseMapper<Judge> {

    default Judge getBySceneType(Long businessId, Long problemId, JudgeSceneType judgeSceneType) {
        QueryWrapper queryWrapper = QueryWrapper.create();
        queryWrapper.eq(Judge::getBusinessId, businessId);
        queryWrapper.eq(Judge::getProblemId, problemId);
        queryWrapper.eq(Judge::getSceneType, judgeSceneType);
        return this.selectOneByQuery(queryWrapper);
    }

    default Long countAc(){
        return this.selectCountByQuery(QueryWrapper.create().eq(Judge::getStatus, JudgeStatus.STATUS_ACCEPTED.getCode()));
    }
}