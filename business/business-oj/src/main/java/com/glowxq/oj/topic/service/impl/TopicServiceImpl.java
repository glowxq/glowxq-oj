package com.glowxq.oj.topic.service.impl;

import com.glowxq.core.common.entity.ImportExcelDTO;
import com.glowxq.core.common.entity.PageResult;
import com.glowxq.core.common.entity.SelectIdsDTO;
import com.glowxq.core.common.enums.CommonResponseEnum;
import com.glowxq.core.common.exception.common.BusinessException;
import com.glowxq.core.util.*;
import com.glowxq.excel.core.ExcelResult;
import com.glowxq.excel.utils.ExcelUtils;
import com.glowxq.oj.common.enums.OjTagBind;

import com.glowxq.oj.problem.pojo.po.Problem;
import com.glowxq.oj.problem.service.ProblemService;
import com.glowxq.oj.topic.mapper.TopicMapper;
import com.glowxq.oj.topic.pojo.dto.*;
import com.glowxq.oj.topic.pojo.po.Topic;
import com.glowxq.oj.topic.pojo.po.TopicInfo;
import com.glowxq.oj.topic.pojo.po.TopicProblem;
import com.glowxq.oj.topic.pojo.po.TopicSubmit;
import com.glowxq.oj.topic.pojo.vo.TopicVO;
import com.glowxq.oj.topic.service.TopicInfoService;
import com.glowxq.oj.topic.service.TopicProblemService;
import com.glowxq.oj.topic.service.TopicService;
import com.glowxq.oj.topic.service.TopicSubmitService;
import com.glowxq.oj.user.pojo.po.UserInfo;
import com.glowxq.oj.user.service.UserInfoService;
import com.glowxq.security.core.util.LoginUtils;
import com.glowxq.system.meta.pojo.po.MetaTag;
import com.glowxq.system.meta.service.MetaTagService;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

/**
 * <p>
 * 主题 服务实现类
 * </p>
 *
 * @author glowxq
 * @since 2025-04-12
 */
@Service
@RequiredArgsConstructor
public class TopicServiceImpl extends ServiceImpl<TopicMapper, Topic> implements TopicService {



    private final MetaTagService metaTagService;

    private final ProblemService problemService;

    private final TopicInfoService topicInfoService;

    private final UserInfoService userInfoService;

    private final TopicSubmitService topicSubmitService;

    private final TopicProblemService topicProblemService;

    private static QueryWrapper buildQueryWrapper(TopicListDTO dto) {
        QueryWrapper wrapper = QueryWrapper.create().from(Topic.class);
        wrapper.like(Topic::getName, dto.getName(), Utils.isNotNull(dto.getName()));
        wrapper.eq(Topic::getCode, dto.getCode(), Utils.isNotNull(dto.getCode()));
        wrapper.eq(Topic::getPrincipalUserId, dto.getPrincipalUserId(), Utils.isNotNull(dto.getPrincipalUserId()));
        wrapper.like(Topic::getPrincipalName, dto.getPrincipalName(), Utils.isNotNull(dto.getPrincipalName()));
        wrapper.eq(Topic::getPassword, dto.getPassword(), Utils.isNotNull(dto.getPassword()));
        wrapper.eq(Topic::getDescription, dto.getDescription(), Utils.isNotNull(dto.getDescription()));
        wrapper.eq(Topic::getTimeRangeType, dto.getTimeRangeType(), Utils.isNotNull(dto.getTimeRangeType()));
        if (StringUtils.isNotBlank(dto.getTopicType())) {
            wrapper.in(Topic::getTopicType, dto.topicType().queryListCode());
        }
        wrapper.eq(Topic::getTopicJudgeType, dto.getTopicJudgeType(), Utils.isNotNull(dto.getTopicJudgeType()));
        wrapper.eq(Topic::getSealedTime, dto.getSealedTime(), Utils.isNotNull(dto.getSealedTime()));
        wrapper.eq(Topic::getTimeLimit, dto.getTimeLimit(), Utils.isNotNull(dto.getTimeLimit()));
        wrapper.eq(Topic::getPunishmentTime, dto.getPunishmentTime(), Utils.isNotNull(dto.getPunishmentTime()));
        wrapper.eq(Topic::getOiScoreType, dto.getOiScoreType(), Utils.isNotNull(dto.getOiScoreType()));
        wrapper.eq(Topic::getColor, dto.getColor(), Utils.isNotNull(dto.getColor()));
        wrapper.or(qw -> {
            qw.eq(Topic::getCommon, dto.getCommon());
        });
        wrapper.eq(Topic::getEnable, dto.getEnable(), Utils.isNotNull(dto.getEnable()));
        wrapper.between(Topic::getStartTime,
                dto.getStartTimeStart(),
                dto.getStartTimeEnd(),
                Utils.isNotNull(dto.getStartTimeStart()) && Utils.isNotNull(dto.getStartTimeEnd()));
        wrapper.between(Topic::getEndTime,
                dto.getEndTimeStart(),
                dto.getEndTimeEnd(),
                Utils.isNotNull(dto.getEndTimeStart()) && Utils.isNotNull(dto.getEndTimeEnd()));
        return wrapper;
    }

    @Override
    public void create(TopicCreateDTO dto) {
        Topic topic = BeanCopyUtils.copy(dto, Topic.class);
        this.save(topic);

        metaTagService.bindTags(topic.getId(), dto.getTagIds(), OjTagBind.Topic);
        topicProblemService.bindTopicProblem(topic.getId(), dto.getProblemIds());
    }

    @Override
    public void update(TopicUpdateDTO dto) {
        Topic topic = BeanCopyUtils.copy(dto, Topic.class);
        QueryWrapper wrapper;
        // id有效性校验
        wrapper = QueryWrapper.create()
                              .eq(Topic::getId, dto.getId());
        CommonResponseEnum.INVALID_ID.assertTrue(count(wrapper) <= 0);

        saveOrUpdate(topic);

        // 解绑数据
        metaTagService.unBindAll(topic.getId(), OjTagBind.Topic);
        topicProblemService.unBindAllByTopicId(topic.getId());

        // 重新绑定数据
        metaTagService.bindTags(topic.getId(), dto.getTagIds(), OjTagBind.Topic);
        topicProblemService.bindTopicProblem(topic.getId(), dto.getProblemIds());
    }

    @Override
    public PageResult<TopicVO> page(TopicListDTO dto) {
        QueryWrapper queryWrapper = buildQueryWrapper(dto);
        List<Long> topicIdsByTag = metaTagService.listBusinessIdByTagIds(dto.getTagIds(), OjTagBind.Topic);
        queryWrapper.in(Topic::getId, topicIdsByTag, CollectionUtils.isNotEmpty(topicIdsByTag));
        Page<TopicVO> page = pageAs(PageUtils.getPage(dto), queryWrapper, TopicVO.class);
        return PageUtils.getPageResult(page);
    }

    @Override
    public List<TopicVO> list(TopicListDTO dto) {
        return listAs(buildQueryWrapper(dto), TopicVO.class);
    }

    @Override
    public void remove(SelectIdsDTO dto) {
        CommonResponseEnum.INVALID_ID.assertTrue(dto.getIds().isEmpty());
        removeByIds(dto.getIds());

        metaTagService.unBindsAll(dto.getIds(), OjTagBind.Topic);
    }

    @Override
    public TopicVO detail(Long id) {
        UserInfo userInfo = userInfoService.getById(LoginUtils.getUserId());
        // auto 主题信息
        TopicInfo topicInfo = topicInfoService.autoTopicId(id, userInfo);
        Topic topic = getById(id);
        LocalDateTime startTime = topicInfo.getStartTime();
        // 进行时间校验
        topic.checkTimeRange(startTime);

        CommonResponseEnum.INVALID_ID.assertNull(topic);
        TopicVO topicVO = BeanCopyUtils.copy(topic, TopicVO.class);

        List<MetaTag> metaTagList = metaTagService.listByBusinessId(topicVO.getId(), OjTagBind.Topic);
        topicVO.setProblemList(this.listProblems(topic.getId()));

        topicVO.setTagList(metaTagList);
        return topicVO;
    }

    @SneakyThrows
    @Override
    public void importExcel(ImportExcelDTO dto) {
        ExcelResult<TopicImportDTO> excelResult = ExcelUtils.importExcel(dto.getFile().getInputStream(), TopicImportDTO.class, true);
        List<TopicImportDTO> list = excelResult.getList();
        List<String> errorList = excelResult.getErrorList();
        String analysis = excelResult.getAnalysis();
        System.out.println(" analysis : " + analysis);
        System.out.println(" isCover : " + dto.getIsCover());
    }

    @SneakyThrows
    @Override
    public void exportExcel(TopicListDTO dto, HttpServletResponse response) {
        List<TopicVO> list = list(dto);
        String fileName = "主题模板";
        OutputStream os = FileUtils.getOutputStream(response, fileName + ".xlsx");
        ExcelUtils.exportExcel(list, "主题", TopicVO.class, os);
    }

    @Override
    public List<Problem> listProblems(Long topicId) {
        List<TopicProblem> topicProblemList = topicProblemService.listByTopicId(topicId);
        List<Long> problemIds = topicProblemList.stream().map(TopicProblem::getProblemId).toList();
        return problemService.listByIds(problemIds);
    }

    @Override
    public List<TopicInfo> topicRank(TopicRankDTO dto) {
        return topicInfoService.topicRank(dto);
    }

    @Override
    public List<TopicSubmit> submitTopicInfo(Long topicId) {
        return topicSubmitService.submitTopicInfo(topicId);
    }

    @Override
    public List<Topic> homeTopic(Integer num) {
        return mapper.listByHome(num);
    }

    @Override
    public List<Topic> listByIds(Collection<? extends Serializable> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return List.of();
        }
        return super.listByIds(ids);
    }
}