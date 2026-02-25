package com.glowxq.oj.topic.service;

import com.glowxq.core.common.entity.ImportExcelDTO;
import com.glowxq.core.common.entity.PageResult;
import com.glowxq.core.common.entity.SelectIdsDTO;
import com.glowxq.oj.judge.pojo.po.Judge;
import com.glowxq.oj.topic.pojo.dto.TopicSubmitCreateDTO;
import com.glowxq.oj.topic.pojo.dto.TopicSubmitListDTO;
import com.glowxq.oj.topic.pojo.dto.TopicSubmitUpdateDTO;
import com.glowxq.oj.topic.pojo.po.TopicSubmit;
import com.glowxq.oj.topic.pojo.vo.TopicSubmitVO;
import com.mybatisflex.core.service.IService;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

/**
 * <p>
 * 主题测评记录 Service
 * </p>
 *
 * @author glowxq
 * @since 2025-04-12
 */
public interface TopicSubmitService extends IService<TopicSubmit> {

    void create(TopicSubmitCreateDTO dto);

    void update(TopicSubmitUpdateDTO dto);

    PageResult<TopicSubmitVO> page(TopicSubmitListDTO dto);

    List<TopicSubmitVO> list(TopicSubmitListDTO dto);

    void remove(SelectIdsDTO dto);

    TopicSubmitVO detail(Object id);

    void importExcel(ImportExcelDTO dto);

    void exportExcel(TopicSubmitListDTO dto, HttpServletResponse response);

    Integer calculatePunishmentTime(Judge judge, Integer punishmentTime);

    List<TopicSubmit> submitTopicInfo(Long topicId);
}