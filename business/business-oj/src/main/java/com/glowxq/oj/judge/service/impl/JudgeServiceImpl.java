package com.glowxq.oj.judge.service.impl;

import com.glowxq.core.common.entity.ImportExcelDTO;
import com.glowxq.core.common.entity.PageResult;
import com.glowxq.core.common.entity.SelectIdsDTO;
import com.glowxq.core.common.enums.CommonResponseEnum;
import com.glowxq.core.common.exception.common.AlertsException;
import com.glowxq.core.util.*;
import com.glowxq.excel.core.ExcelResult;
import com.glowxq.excel.utils.ExcelUtils;

import com.glowxq.oj.judge.enums.JudgeSceneType;
import com.glowxq.oj.judge.enums.JudgeStatus;
import com.glowxq.oj.judge.mapper.JudgeMapper;
import com.glowxq.oj.judge.pojo.dto.JudgeCreateDTO;
import com.glowxq.oj.judge.pojo.dto.JudgeImportDTO;
import com.glowxq.oj.judge.pojo.dto.JudgeListDTO;
import com.glowxq.oj.judge.pojo.dto.ManualEvaluationDTO;
import com.glowxq.oj.judge.pojo.po.Judge;
import com.glowxq.oj.judge.pojo.po.JudgeCase;
import com.glowxq.oj.judge.pojo.vo.JudgeVO;
import com.glowxq.oj.judge.service.JudgeCaseService;
import com.glowxq.oj.judge.service.JudgeService;
import com.glowxq.security.core.util.LoginUtils;
import com.glowxq.system.meta.enums.SystemTagBind;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.util.List;

/**
 * <p>
 * 测评记录 服务实现类
 * </p>
 *
 * @author glowxq
 * @since 2025-03-17
 */
@Service
@RequiredArgsConstructor
@Slf4j(topic = "judge")
public class JudgeServiceImpl extends ServiceImpl<JudgeMapper, Judge> implements JudgeService {

    private final JudgeCaseService judgeCaseService;



    private static QueryWrapper buildQueryWrapper(JudgeListDTO dto) {
        QueryWrapper wrapper = QueryWrapper.create().from(Judge.class);
        wrapper.eq(Judge::getProblemId, dto.getProblemId(), Utils.isNotNull(dto.getProblemId()));
        wrapper.eq(Judge::getProblemKey, dto.getProblemKey(), Utils.isNotNull(dto.getProblemKey()));
        wrapper.eq(Judge::getBusinessId, dto.getBusinessId(), Utils.isNotNull(dto.getBusinessId()));
        wrapper.like(Judge::getName, dto.getName(), Utils.isNotNull(dto.getName()));
        wrapper.eq(Judge::getProblemType, dto.getProblemType(), Utils.isNotNull(dto.getProblemType()));
        wrapper.eq(Judge::getStatus, dto.getStatus(), Utils.isNotNull(dto.getStatus()));
        wrapper.eq(Judge::getUserId, LoginUtils.getUserId(), dto.isMyJudge());
        wrapper.eq(Judge::getManualEvaluation, dto.getManualEvaluation(), Utils.isNotNull(dto.getManualEvaluation()));
        wrapper.eq(Judge::getProblemTitle, dto.getProblemTitle(), StringUtils.isNotEmpty(dto.getProblemTitle()));
        // wrapper.eq(Judge::getId,)
        wrapper.orderBy(Judge::getId).desc();

        return wrapper;
    }

    @Override
    public void create(JudgeCreateDTO dto) {
        Judge judge = BeanCopyUtils.copy(dto, Judge.class);
        save(judge);
    }

    @Override
    public void update(ManualEvaluationDTO dto) {
        Judge judge = BeanCopyUtils.copy(dto, Judge.class);
        QueryWrapper wrapper;
        // id有效性校验
        wrapper = QueryWrapper.create()
                              .eq(Judge::getId, dto.getId());
        CommonResponseEnum.INVALID_ID.assertTrue(count(wrapper) <= 0);

        saveOrUpdate(judge);
    }

    @Override
    public PageResult<JudgeVO> page(JudgeListDTO dto) {
        QueryWrapper queryWrapper = buildQueryWrapper(dto);

        Page<JudgeVO> page = pageAs(PageUtils.getPage(dto), queryWrapper, JudgeVO.class);
        return PageUtils.getPageResult(page);
    }

    @Override
    public List<JudgeVO> list(JudgeListDTO dto) {
        return listAs(buildQueryWrapper(dto), JudgeVO.class);
    }

    @Override
    public void remove(SelectIdsDTO dto) {
        CommonResponseEnum.INVALID_ID.assertTrue(dto.getIds().isEmpty());
        removeByIds(dto.getIds());
    }

    @Override
    public JudgeVO detail(Long id) {
        Judge judge = getById(id);
        CommonResponseEnum.INVALID_ID.assertNull(judge);
        List<JudgeCase> judgeCaseList = judgeCaseService.listByJudgeId(id);
        JudgeVO judgeVO = BeanCopyUtils.copy(judge, JudgeVO.class);
        judgeVO.setJudgeCaseList(judgeCaseList);
        return judgeVO;
    }

    @SneakyThrows
    @Override
    public void importExcel(ImportExcelDTO dto) {
        ExcelResult<JudgeImportDTO> excelResult = ExcelUtils.importExcel(dto.getFile().getInputStream(), JudgeImportDTO.class, true);
        List<JudgeImportDTO> list = excelResult.getList();
        List<String> errorList = excelResult.getErrorList();
        String analysis = excelResult.getAnalysis();
        System.out.println(" analysis : " + analysis);
        System.out.println(" isCover : " + dto.getIsCover());
    }

    @SneakyThrows
    @Override
    public void exportExcel(JudgeListDTO dto, HttpServletResponse response) {
        List<JudgeVO> list = list(dto);
        String fileName = "测评记录模板";
        OutputStream os = FileUtils.getOutputStream(response, fileName + ".xlsx");
        ExcelUtils.exportExcel(list, "测评记录", JudgeVO.class, os);
    }

    @Override
    public void updateCompiling(Judge judge) {
        Long judgeId = judge.getId();
        log.info("{}:更新判题记录为编译中 start:{}", judgeId, judge);
        judge = mapper.selectOneById(judgeId);
        if (judge == null) {
            log.info("数据库查询不到记录");
            throw new AlertsException(String.format("id[%s]数据库查询不到记录,judge:%s", judgeId, JsonUtils.toJsonString(judge)));
        }
        JudgeStatus status = judge.status();
        if (JudgeStatus.STATUS_CANCELLED.equals(status)) {
            log.info("测评:{} 题目:{} 该判题记录已取消，无需更新状态:{}", judgeId, judge.getProblemId(), judge);
            return;
        }
        // 标志该判题过程进入编译阶段
        judge.setStatus(JudgeStatus.STATUS_COMPILING.getStatus());
        judge.setJudgeServer("");
        mapper.update(judge);
        log.info("{}:更新判题记录为编译中 end:{}", judgeId, judge);
    }

    @Override
    public void updateJudging(Judge judge) {
        log.info("{}:更新判题记录为判题中 start:{}", judge.getId(), judge);
        judge.setStatus(JudgeStatus.STATUS_JUDGING.getStatus());
        mapper.update(judge);
        log.info("{}:更新判题记录为判题中 end:{}", judge.getId(), judge);
    }

    @Override
    public void manualEvaluation(ManualEvaluationDTO dto) {
        Judge judge = mapper.selectOneById(dto.getId());
        judge.setManualEvaluation(true);
        judge.setStatus(dto.getJudgeStatus());
        judge.setScore(dto.getScore());
        mapper.update(judge);
    }

    @Override
    public Judge getBySceneType(Long businessId, Long problemId, JudgeSceneType judgeSceneType) {
        return mapper.getBySceneType(businessId, problemId, judgeSceneType);
    }

    @Override
    public Long countAc() {
        return mapper.countAc();
    }
}