package com.glowxq.oj.topic.service;

import com.glowxq.core.common.entity.ImportExcelDTO;
import com.glowxq.core.common.entity.PageResult;
import com.glowxq.core.common.entity.SelectIdsDTO;
import com.glowxq.oj.problem.pojo.po.Problem;
import com.glowxq.oj.topic.pojo.dto.TopicCreateDTO;
import com.glowxq.oj.topic.pojo.dto.TopicListDTO;
import com.glowxq.oj.topic.pojo.dto.TopicRankDTO;
import com.glowxq.oj.topic.pojo.dto.TopicUpdateDTO;
import com.glowxq.oj.topic.pojo.po.Topic;
import com.glowxq.oj.topic.pojo.po.TopicInfo;
import com.glowxq.oj.topic.pojo.po.TopicSubmit;
import com.glowxq.oj.topic.pojo.vo.TopicVO;
import com.mybatisflex.core.service.IService;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

/**
 * <p>
 * 主题 Service
 * </p>
 *
 * @author glowxq
 * @since 2025-04-12
 */
public interface TopicService extends IService<Topic> {

    void create(TopicCreateDTO dto);

    void update(TopicUpdateDTO dto);

    PageResult<TopicVO> page(TopicListDTO dto);

    List<TopicVO> list(TopicListDTO dto);

    void remove(SelectIdsDTO dto);

    TopicVO detail(Long id);

    void importExcel(ImportExcelDTO dto);

    void exportExcel(TopicListDTO dto, HttpServletResponse response);

    List<Problem> listProblems(Long topicId);

    List<TopicInfo> topicRank(TopicRankDTO dto);

    List<TopicSubmit> submitTopicInfo(Long topicId);

    List<Topic> homeTopic(Integer num);
}