package com.glowxq.oj.topic.service;

import com.glowxq.core.common.entity.ImportExcelDTO;
import com.glowxq.core.common.entity.PageResult;
import com.glowxq.core.common.entity.SelectIdsDTO;
import com.glowxq.oj.judge.pojo.po.Judge;
import com.glowxq.oj.topic.pojo.dto.TopicInfoCreateDTO;
import com.glowxq.oj.topic.pojo.dto.TopicInfoListDTO;
import com.glowxq.oj.topic.pojo.dto.TopicInfoUpdateDTO;
import com.glowxq.oj.topic.pojo.dto.TopicRankDTO;
import com.glowxq.oj.topic.pojo.po.TopicInfo;
import com.glowxq.oj.topic.pojo.po.TopicSubmit;
import com.glowxq.oj.topic.pojo.vo.TopicInfoVO;
import com.glowxq.oj.user.pojo.po.UserInfo;
import com.mybatisflex.core.service.IService;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

/**
 * <p>
 * 主题数据 Service
 * </p>
 *
 * @author glowxq
 * @since 2025-04-12
 */
public interface TopicInfoService extends IService<TopicInfo> {

    void create(TopicInfoCreateDTO dto);

    void update(TopicInfoUpdateDTO dto);

    PageResult<TopicInfoVO> page(TopicInfoListDTO dto);

    List<TopicInfoVO> list(TopicInfoListDTO dto);

    void remove(SelectIdsDTO dto);

    TopicInfoVO detail(Object id);

    void importExcel(ImportExcelDTO dto);

    void exportExcel(TopicInfoListDTO dto, HttpServletResponse response);

    void autoJudge(Judge judge, TopicSubmit topicSubmit, UserInfo userInfo);

    TopicInfo autoTopicId(Long topicId, UserInfo userInfo);

    List<TopicInfo> topicRank(TopicRankDTO dto);
}