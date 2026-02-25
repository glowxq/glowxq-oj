package com.glowxq.oj.topic.service;

import com.glowxq.core.common.entity.ImportExcelDTO;
import com.glowxq.core.common.entity.PageResult;
import com.glowxq.core.common.entity.SelectIdsDTO;
import com.glowxq.oj.topic.pojo.dto.TopicProblemCreateDTO;
import com.glowxq.oj.topic.pojo.dto.TopicProblemListDTO;
import com.glowxq.oj.topic.pojo.dto.TopicProblemUpdateDTO;
import com.glowxq.oj.topic.pojo.po.TopicProblem;
import com.glowxq.oj.topic.pojo.vo.TopicProblemVO;
import com.mybatisflex.core.service.IService;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

/**
 * <p>
 * 主题题目 Service
 * </p>
 *
 * @author glowxq
 * @since 2025-04-12
 */
public interface TopicProblemService extends IService<TopicProblem> {

    void create(TopicProblemCreateDTO dto);

    void update(TopicProblemUpdateDTO dto);

    PageResult<TopicProblemVO> page(TopicProblemListDTO dto);

    List<TopicProblemVO> list(TopicProblemListDTO dto);

    void remove(SelectIdsDTO dto);

    TopicProblemVO detail(Object id);

    void importExcel(ImportExcelDTO dto);

    void exportExcel(TopicProblemListDTO dto, HttpServletResponse response);

    void bindTopicProblem(Long topicId, List<Long> problemIds);

    void unBindAllByTopicId(Long id);

    List<TopicProblem> listByTopicId(Long topicId);
}