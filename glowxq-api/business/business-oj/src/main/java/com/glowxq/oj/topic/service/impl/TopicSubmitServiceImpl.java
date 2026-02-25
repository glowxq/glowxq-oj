package com.glowxq.oj.topic.service.impl;

import com.glowxq.core.common.entity.ImportExcelDTO;
import com.glowxq.core.common.entity.PageResult;
import com.glowxq.core.common.entity.SelectIdsDTO;
import com.glowxq.core.common.enums.CommonResponseEnum;
import com.glowxq.core.util.BeanCopyUtils;
import com.glowxq.core.util.FileUtils;
import com.glowxq.core.util.PageUtils;
import com.glowxq.core.util.Utils;
import com.glowxq.excel.core.ExcelResult;
import com.glowxq.excel.utils.ExcelUtils;
import com.glowxq.oj.judge.enums.JudgeStatus;
import com.glowxq.oj.judge.pojo.po.Judge;
import com.glowxq.oj.topic.mapper.TopicSubmitMapper;
import com.glowxq.oj.topic.pojo.dto.TopicSubmitCreateDTO;
import com.glowxq.oj.topic.pojo.dto.TopicSubmitImportDTO;
import com.glowxq.oj.topic.pojo.dto.TopicSubmitListDTO;
import com.glowxq.oj.topic.pojo.dto.TopicSubmitUpdateDTO;
import com.glowxq.oj.topic.pojo.po.TopicSubmit;
import com.glowxq.oj.topic.pojo.vo.TopicSubmitVO;
import com.glowxq.oj.topic.service.TopicSubmitService;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 主题测评记录 服务实现类
 * </p>
 *
 * @author glowxq
 * @since 2025-04-12
 */
@Service
@RequiredArgsConstructor
public class TopicSubmitServiceImpl extends ServiceImpl<TopicSubmitMapper, TopicSubmit> implements TopicSubmitService {

    private static QueryWrapper buildQueryWrapper(TopicSubmitListDTO dto) {
        QueryWrapper wrapper = QueryWrapper.create().from(TopicSubmit.class);
        wrapper.eq(TopicSubmit::getTopicId, dto.getTopicId(), Utils.isNotNull(dto.getTopicId()));
        wrapper.eq(TopicSubmit::getProblemId, dto.getProblemId(), Utils.isNotNull(dto.getProblemId()));
        wrapper.eq(TopicSubmit::getProblemKey, dto.getProblemKey(), Utils.isNotNull(dto.getProblemKey()));
        wrapper.eq(TopicSubmit::getProblemTitle, dto.getProblemTitle(), Utils.isNotNull(dto.getProblemTitle()));
        wrapper.eq(TopicSubmit::getProblemType, dto.getProblemType(), Utils.isNotNull(dto.getProblemType()));
        wrapper.eq(TopicSubmit::getTopicJudgeType, dto.getTopicJudgeType(), Utils.isNotNull(dto.getTopicJudgeType()));
        wrapper.eq(TopicSubmit::getUserId, dto.getUserId(), Utils.isNotNull(dto.getUserId()));
        wrapper.like(TopicSubmit::getName, dto.getName(), Utils.isNotNull(dto.getName()));
        wrapper.like(TopicSubmit::getNickName, dto.getNickName(), Utils.isNotNull(dto.getNickName()));
        wrapper.eq(TopicSubmit::getJudgeStatus, dto.getJudgeStatus(), Utils.isNotNull(dto.getJudgeStatus()));
        wrapper.eq(TopicSubmit::getScore, dto.getScore(), Utils.isNotNull(dto.getScore()));
        wrapper.eq(TopicSubmit::getUseTime, dto.getUseTime(), Utils.isNotNull(dto.getUseTime()));
        wrapper.eq(TopicSubmit::getPunishmentTime, dto.getPunishmentTime(), Utils.isNotNull(dto.getPunishmentTime()));
        return wrapper;
    }

    @Override
    public void create(TopicSubmitCreateDTO dto) {
        TopicSubmit topicSubmit = BeanCopyUtils.copy(dto, TopicSubmit.class);
        save(topicSubmit);
    }

    @Override
    public void update(TopicSubmitUpdateDTO dto) {
        TopicSubmit topicSubmit = BeanCopyUtils.copy(dto, TopicSubmit.class);
        QueryWrapper wrapper;
        // id有效性校验
        wrapper = QueryWrapper.create()
                              .eq(TopicSubmit::getId, dto.getId());
        CommonResponseEnum.INVALID_ID.assertTrue(count(wrapper) <= 0);

        saveOrUpdate(topicSubmit);
    }

    @Override
    public PageResult<TopicSubmitVO> page(TopicSubmitListDTO dto) {
        Page<TopicSubmitVO> page = pageAs(PageUtils.getPage(dto), buildQueryWrapper(dto), TopicSubmitVO.class);
        return PageUtils.getPageResult(page);
    }

    @Override
    public List<TopicSubmitVO> list(TopicSubmitListDTO dto) {
        return listAs(buildQueryWrapper(dto), TopicSubmitVO.class);
    }

    @Override
    public void remove(SelectIdsDTO dto) {
        CommonResponseEnum.INVALID_ID.assertTrue(dto.getIds().isEmpty());
        removeByIds(dto.getIds());
    }

    @Override
    public TopicSubmitVO detail(Object id) {
        TopicSubmit topicSubmit = getById((Serializable) id);
        CommonResponseEnum.INVALID_ID.assertNull(topicSubmit);
        return BeanCopyUtils.copy(topicSubmit, TopicSubmitVO.class);
    }

    @SneakyThrows
    @Override
    public void importExcel(ImportExcelDTO dto) {
        ExcelResult<TopicSubmitImportDTO> excelResult = ExcelUtils.importExcel(dto.getFile().getInputStream(), TopicSubmitImportDTO.class, true);
        List<TopicSubmitImportDTO> list = excelResult.getList();
        List<String> errorList = excelResult.getErrorList();
        String analysis = excelResult.getAnalysis();
        System.out.println(" analysis : " + analysis);
        System.out.println(" isCover : " + dto.getIsCover());
    }

    @SneakyThrows
    @Override
    public void exportExcel(TopicSubmitListDTO dto, HttpServletResponse response) {
        List<TopicSubmitVO> list = list(dto);
        String fileName = "主题测评记录模板";
        OutputStream os = FileUtils.getOutputStream(response, fileName + ".xlsx");
        ExcelUtils.exportExcel(list, "主题测评记录", TopicSubmitVO.class, os);
    }

    @Override
    public Integer calculatePunishmentTime(Judge judge, Integer punishmentTime) {
        if (judge.isNotAc()) {
            return 0;
        }
        List<TopicSubmit> topicSubmitList = mapper.listByTopicIdProblemIdJudgeStatus(judge.getBusinessId(), judge.getProblemId(),
                JudgeStatus.listPunishmentTimeStatus());
        return topicSubmitList.size() * punishmentTime;
    }

    @Override
    public List<TopicSubmit> submitTopicInfo(Long topicId) {
        return mapper.listByTopicIdMaxUserScore(topicId);
    }
}