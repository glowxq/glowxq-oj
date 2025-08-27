package com.glowxq.oj.topic.service.impl;

import com.glowxq.core.common.entity.BaseUserInfo;
import com.glowxq.core.common.entity.ImportExcelDTO;
import com.glowxq.core.common.entity.PageResult;
import com.glowxq.core.common.entity.SelectIdsDTO;
import com.glowxq.core.common.enums.CommonResponseEnum;
import com.glowxq.core.common.feishu.utils.FeishuMessageUtils;
import com.glowxq.core.util.BeanCopyUtils;
import com.glowxq.core.util.FileUtils;
import com.glowxq.core.util.PageUtils;
import com.glowxq.core.util.Utils;
import com.glowxq.excel.core.ExcelResult;
import com.glowxq.excel.utils.ExcelUtils;
import com.glowxq.oj.judge.pojo.po.Judge;
import com.glowxq.oj.topic.enums.TopicInfoStatus;
import com.glowxq.oj.topic.mapper.TopicInfoMapper;
import com.glowxq.oj.topic.pojo.dto.*;
import com.glowxq.oj.topic.pojo.po.TopicInfo;
import com.glowxq.oj.topic.pojo.po.TopicSubmit;
import com.glowxq.oj.topic.pojo.vo.TopicInfoVO;
import com.glowxq.oj.topic.service.TopicInfoService;
import com.glowxq.oj.user.pojo.po.UserInfo;
import com.glowxq.security.core.util.LoginUtils;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 主题数据 服务实现类
 * </p>
 *
 * @author glowxq
 * @since 2025-04-12
 */
@Service
@RequiredArgsConstructor
public class TopicInfoServiceImpl extends ServiceImpl<TopicInfoMapper, TopicInfo> implements TopicInfoService {

    private static QueryWrapper buildQueryWrapper(TopicInfoListDTO dto) {
        QueryWrapper wrapper = QueryWrapper.create().from(TopicInfo.class);
        wrapper.eq(TopicInfo::getTopicId, dto.getTopicId(), Utils.isNotNull(dto.getTopicId()));
        wrapper.eq(TopicInfo::getUserId, dto.getUserId(), Utils.isNotNull(dto.getUserId()));
        wrapper.eq(TopicInfo::getAvatar, dto.getAvatar(), Utils.isNotNull(dto.getAvatar()));
        wrapper.like(TopicInfo::getName, dto.getName(), Utils.isNotNull(dto.getName()));
        wrapper.like(TopicInfo::getNickName, dto.getNickName(), Utils.isNotNull(dto.getNickName()));
        wrapper.eq(TopicInfo::getScore, dto.getScore(), Utils.isNotNull(dto.getScore()));
        wrapper.eq(TopicInfo::getAcNum, dto.getAcNum(), Utils.isNotNull(dto.getAcNum()));
        wrapper.eq(TopicInfo::getSubmitNum, dto.getSubmitNum(), Utils.isNotNull(dto.getSubmitNum()));
        wrapper.eq(TopicInfo::getUseTime, dto.getUseTime(), Utils.isNotNull(dto.getUseTime()));
        wrapper.eq(TopicInfo::getPunishmentTime, dto.getPunishmentTime(), Utils.isNotNull(dto.getPunishmentTime()));
        wrapper.eq(TopicInfo::getStatus, dto.getStatus(), Utils.isNotNull(dto.getStatus()));
        wrapper.between(TopicInfo::getStartTime,
                dto.getStartTimeStart(),
                dto.getStartTimeEnd(),
                Utils.isNotNull(dto.getStartTimeStart()) && Utils.isNotNull(dto.getStartTimeEnd()));
        wrapper.between(TopicInfo::getEndTime,
                dto.getEndTimeStart(),
                dto.getEndTimeEnd(),
                Utils.isNotNull(dto.getEndTimeStart()) && Utils.isNotNull(dto.getEndTimeEnd()));
        wrapper.eq(TopicInfo::getAutoSubmit, dto.getAutoSubmit(), Utils.isNotNull(dto.getAutoSubmit()));
        return wrapper;
    }

    @Override
    public void create(TopicInfoCreateDTO dto) {
        BaseUserInfo userInfo = LoginUtils.getLoginUser().getUserInfo();
        TopicInfo topicInfo = BeanCopyUtils.copy(dto, TopicInfo.class);
        topicInfo.setTopicId(0L);
        topicInfo.setUserId(userInfo.getId());
        topicInfo.setAvatar(userInfo.getLogo());
        topicInfo.setName(userInfo.getName());
        topicInfo.setNickName(userInfo.getNickname());
        topicInfo.setStatus(TopicInfoStatus.NotStarted.getCode());
        save(topicInfo);
    }

    @Override
    public void update(TopicInfoUpdateDTO dto) {
        TopicInfo topicInfo = BeanCopyUtils.copy(dto, TopicInfo.class);
        QueryWrapper wrapper;
        // id有效性校验
        wrapper = QueryWrapper.create()
                              .eq(TopicInfo::getId, dto.getId());
        CommonResponseEnum.INVALID_ID.assertTrue(count(wrapper) <= 0);

        saveOrUpdate(topicInfo);
    }

    @Override
    public PageResult<TopicInfoVO> page(TopicInfoListDTO dto) {
        Page<TopicInfoVO> page = pageAs(PageUtils.getPage(dto), buildQueryWrapper(dto), TopicInfoVO.class);
        return PageUtils.getPageResult(page);
    }

    @Override
    public List<TopicInfoVO> list(TopicInfoListDTO dto) {
        return listAs(buildQueryWrapper(dto), TopicInfoVO.class);
    }

    @Override
    public void remove(SelectIdsDTO dto) {
        CommonResponseEnum.INVALID_ID.assertTrue(dto.getIds().isEmpty());
        removeByIds(dto.getIds());
    }

    @Override
    public TopicInfoVO detail(Object id) {
        TopicInfo topicInfo = getById((Serializable) id);
        CommonResponseEnum.INVALID_ID.assertNull(topicInfo);
        return BeanCopyUtils.copy(topicInfo, TopicInfoVO.class);
    }

    @SneakyThrows
    @Override
    public void importExcel(ImportExcelDTO dto) {
        ExcelResult<TopicInfoImportDTO> excelResult = ExcelUtils.importExcel(dto.getFile().getInputStream(), TopicInfoImportDTO.class, true);
        List<TopicInfoImportDTO> list = excelResult.getList();
        List<String> errorList = excelResult.getErrorList();
        String analysis = excelResult.getAnalysis();
        System.out.println(" analysis : " + analysis);
        System.out.println(" isCover : " + dto.getIsCover());
    }

    @SneakyThrows
    @Override
    public void exportExcel(TopicInfoListDTO dto, HttpServletResponse response) {
        List<TopicInfoVO> list = list(dto);
        String fileName = "主题数据模板";
        OutputStream os = FileUtils.getOutputStream(response, fileName + ".xlsx");
        ExcelUtils.exportExcel(list, "主题数据", TopicInfoVO.class, os);
    }

    @Override
    public void autoJudge(Judge judge, TopicSubmit topicSubmit, UserInfo userInfo) {
        TopicInfo topicInfo = mapper.getByTopicId(judge.getBusinessId(), judge.getUserId());
        if (topicInfo == null) {
            FeishuMessageUtils.sendInternalMessage("autoJudge topic not fond");
            topicInfo = new TopicInfo();
        }
        topicInfo.setTopicId(judge.getBusinessId());
        topicInfo.setUserId(userInfo.getId());
        topicInfo.setAvatar(userInfo.getColor());
        topicInfo.setName(userInfo.getName());
        topicInfo.setNickName(userInfo.getNickName());
        topicInfo.setEndTime(LocalDateTime.now());
        topicInfo.setAutoSubmit(judge.getAutoSubmit());
        topicInfo.setCreateTime(LocalDateTime.now());
        topicInfo.setUpdateTime(LocalDateTime.now());
        topicInfo.plusScore(judge.getScore());
        topicInfo.plusAcNum(judge.isAc());
        topicInfo.plusSubmitNum();
        topicInfo.plusUseTime(judge.getUseTime());
        topicInfo.plusPunishmentTime(topicSubmit.getPunishmentTime());
        topicInfo.setStatus(TopicInfoStatus.InProgress.getCode());
        topicInfo.setEndTime(judge.getEndTime());
        this.saveOrUpdate(topicInfo);
    }

    @Override
    public TopicInfo autoTopicId(Long topicId, UserInfo userInfo) {
        TopicInfo topicInfo = mapper.getByTopicId(topicId, userInfo.getId());
        if (topicInfo != null) {
            return topicInfo;
        }
        topicInfo = new TopicInfo();
        topicInfo.setTopicId(topicId);
        topicInfo.setUserId(userInfo.getId());
        topicInfo.setAvatar(userInfo.getAvatar());
        topicInfo.setName(userInfo.getName());
        topicInfo.setNickName(userInfo.getNickName());
        topicInfo.setStatus(TopicInfoStatus.NotStarted.getCode());
        topicInfo.setStartTime(LocalDateTime.now());
        topicInfo.setCreateTime(LocalDateTime.now());
        topicInfo.setUpdateTime(LocalDateTime.now());
        this.save(topicInfo);
        return topicInfo;
    }

    @Override
    public List<TopicInfo> topicRank(TopicRankDTO dto) {
        return mapper.topicRank(dto);
    }
}