package com.glowxq.oj.problem.service.impl;

import cn.hutool.core.io.FileUtil;
import com.glowxq.core.common.entity.ImportExcelDTO;
import com.glowxq.core.common.entity.PageResult;
import com.glowxq.core.common.entity.SelectIdsDTO;
import com.glowxq.core.common.enums.CommonResponseEnum;
import com.glowxq.core.common.exception.common.BusinessException;
import com.glowxq.core.util.*;
import com.glowxq.excel.core.ExcelResult;
import com.glowxq.excel.utils.ExcelUtils;
import com.glowxq.oj.common.enums.OjTagBind;

import com.glowxq.oj.problem.enums.FilePath;
import com.glowxq.oj.problem.enums.ProblemCaseType;
import com.glowxq.oj.problem.mapper.ProblemMapper;
import com.glowxq.oj.problem.pojo.dto.*;
import com.glowxq.oj.problem.pojo.po.Problem;
import com.glowxq.oj.problem.pojo.po.ProblemCase;
import com.glowxq.oj.problem.pojo.po.ProblemCodeTemplate;
import com.glowxq.oj.problem.pojo.po.ProblemOption;
import com.glowxq.oj.problem.pojo.vo.ProblemVO;
import com.glowxq.oj.problem.service.ProblemCaseService;
import com.glowxq.oj.problem.service.ProblemCodeTemplateService;
import com.glowxq.oj.problem.service.ProblemOptionService;
import com.glowxq.oj.problem.service.ProblemService;
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
import org.springframework.transaction.annotation.Transactional;

import java.io.OutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * <p>
 * 题目 服务实现类
 * </p>
 *
 * @author glowxq
 * @since 2025-03-17
 */
@Service
@RequiredArgsConstructor
public class ProblemServiceImpl extends ServiceImpl<ProblemMapper, Problem> implements ProblemService {

    private final ProblemCaseService problemCaseService;

    private final ProblemOptionService problemOptionService;

    private final ProblemCodeTemplateService problemCodeTemplateService;



    private final MetaTagService metaTagService;

    private static QueryWrapper buildQueryWrapper(ProblemListDTO dto) {
        QueryWrapper wrapper = QueryWrapper.create().from(Problem.class);
        wrapper.eq(Problem::getProblemKey, dto.getProblemKey(), Utils.isNotNull(dto.getProblemKey()));
        wrapper.eq(Problem::getTitle, dto.getTitle(), Utils.isNotNull(dto.getTitle()));
        wrapper.eq(Problem::getAuthor, dto.getAuthor(), Utils.isNotNull(dto.getAuthor()));
        wrapper.eq(Problem::getProgramType, dto.getProgramType(), Utils.isNotNull(dto.getProgramType()));
        wrapper.eq(Problem::getProblemType, dto.getProblemType(), Utils.isNotNull(dto.getProblemType()));
        wrapper.eq(Problem::getSourceType, dto.getSourceType(), Utils.isNotNull(dto.getSourceType()));
        wrapper.eq(Problem::getTimeLimit, dto.getTimeLimit(), Utils.isNotNull(dto.getTimeLimit()));
        wrapper.eq(Problem::getMemoryLimit, dto.getMemoryLimit(), Utils.isNotNull(dto.getMemoryLimit()));
        wrapper.eq(Problem::getStackLimit, dto.getStackLimit(), Utils.isNotNull(dto.getStackLimit()));
        wrapper.eq(Problem::getDescription, dto.getDescription(), Utils.isNotNull(dto.getDescription()));
        wrapper.eq(Problem::getInput, dto.getInput(), Utils.isNotNull(dto.getInput()));
        wrapper.eq(Problem::getOutput, dto.getOutput(), Utils.isNotNull(dto.getOutput()));
        wrapper.eq(Problem::getExamples, dto.getExamples(), Utils.isNotNull(dto.getExamples()));
        wrapper.eq(Problem::getDifficulty, dto.getDifficulty(), Utils.isNotNull(dto.getDifficulty()));
        wrapper.eq(Problem::getHint, dto.getHint(), Utils.isNotNull(dto.getHint()));
        wrapper.eq(Problem::getAuth, dto.getAuth(), Utils.isNotNull(dto.getAuth()));
        wrapper.eq(Problem::getIoScore, dto.getIoScore(), Utils.isNotNull(dto.getIoScore()));
        wrapper.eq(Problem::getScore, dto.getScore(), Utils.isNotNull(dto.getScore()));
        wrapper.eq(Problem::getSource, dto.getSource(), Utils.isNotNull(dto.getSource()));
        wrapper.eq(Problem::getJudgeMode, dto.getJudgeMode(), Utils.isNotNull(dto.getJudgeMode()));
        wrapper.eq(Problem::getJudgeCaseMode, dto.getJudgeCaseMode(), Utils.isNotNull(dto.getJudgeCaseMode()));
        wrapper.eq(Problem::getUserExtraFile, dto.getUserExtraFile(), Utils.isNotNull(dto.getUserExtraFile()));
        wrapper.eq(Problem::getJudgeExtraFile, dto.getJudgeExtraFile(), Utils.isNotNull(dto.getJudgeExtraFile()));
        wrapper.eq(Problem::getSpjCode, dto.getSpjCode(), Utils.isNotNull(dto.getSpjCode()));
        wrapper.eq(Problem::getSpjLanguage, dto.getSpjLanguage(), Utils.isNotNull(dto.getSpjLanguage()));
        wrapper.eq(Problem::getRemote, dto.getRemote(), Utils.isNotNull(dto.getRemote()));
        wrapper.eq(Problem::getCodeShare, dto.getCodeShare(), Utils.isNotNull(dto.getCodeShare()));
        wrapper.eq(Problem::getRemoveEndBlank, dto.getRemoveEndBlank(), Utils.isNotNull(dto.getRemoveEndBlank()));
        wrapper.eq(Problem::getOpenCaseResult, dto.getOpenCaseResult(), Utils.isNotNull(dto.getOpenCaseResult()));
        wrapper.eq(Problem::getUploadCase, dto.getUploadCase(), Utils.isNotNull(dto.getUploadCase()));
        wrapper.eq(Problem::getGroupProblem, dto.getGroupProblem(), Utils.isNotNull(dto.getGroupProblem()));
        wrapper.eq(Problem::getFileIo, dto.getFileIo(), Utils.isNotNull(dto.getFileIo()));
        wrapper.eq(Problem::getRequireImage, dto.getRequireImage(), Utils.isNotNull(dto.getRequireImage()));
        wrapper.eq(Problem::getCaseVersion, dto.getCaseVersion(), Utils.isNotNull(dto.getCaseVersion()));
        wrapper.eq(Problem::getModifiedUser, dto.getModifiedUser(), Utils.isNotNull(dto.getModifiedUser()));
        wrapper.eq(Problem::getApplyPublicProgress, dto.getApplyPublicProgress(), Utils.isNotNull(dto.getApplyPublicProgress()));
        wrapper.like(Problem::getIoReadFileName, dto.getIoReadFileName(), Utils.isNotNull(dto.getIoReadFileName()));
        wrapper.like(Problem::getIoWriteFileName, dto.getIoWriteFileName(), Utils.isNotNull(dto.getIoWriteFileName()));
        wrapper.orderBy(Problem::getId).desc();
        return wrapper;
    }

    @Override
    @Transactional
    public void create(ProblemCreateUpdateDTO dto) {

    }

    @Override
    public void update(ProblemUpdateDTO dto) {
        Problem problem = BeanCopyUtils.copy(dto, Problem.class);
        QueryWrapper wrapper;
        // id有效性校验
        wrapper = QueryWrapper.create()
                              .eq(Problem::getId, dto.getId());
        CommonResponseEnum.INVALID_ID.assertTrue(count(wrapper) <= 0);

        saveOrUpdate(problem);
    }

    @Override
    public PageResult<ProblemVO> page(ProblemListDTO dto) {
        QueryWrapper queryWrapper = buildQueryWrapper(dto);
        List<Long> tagIds = dto.getTagIds();
        List<Long> problemIds = metaTagService.listBusinessIdByTagIds(tagIds, OjTagBind.Problem);
        if (CollectionUtils.isEmpty(problemIds) && CollectionUtils.isNotEmpty(tagIds)) {
            throw new BusinessException("标签筛选后 没有题目");
        }
        queryWrapper.in(Problem::getId, problemIds, CollectionUtils.isNotEmpty(problemIds));
        Page<ProblemVO> page = pageAs(PageUtils.getPage(dto), queryWrapper, ProblemVO.class);
        return PageUtils.getPageResult(page);
    }

    @Override
    public List<ProblemVO> list(ProblemListDTO dto) {
        return listAs(buildQueryWrapper(dto), ProblemVO.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void remove(SelectIdsDTO dto) {
        CommonResponseEnum.INVALID_ID.assertTrue(dto.getIds().isEmpty());
        boolean b = removeByIds(dto.getIds());
        CommonResponseEnum.INVALID_ID.assertFalse(b);

        metaTagService.unBindsAll(dto.getIds(), OjTagBind.Problem);
        for (Serializable id : dto.getIds()) {
            FileUtil.del(FilePath.TESTCASE_BASE_FOLDER.buildPath(id));
        }
    }

    @Override
    public ProblemCreateUpdateDTO detail(Long problemId) {
        Problem problem = getById(problemId);
        CommonResponseEnum.INVALID_ID.assertNull(problem);

        BeanCopyUtils.copy(problem, ProblemVO.class);
        List<ProblemOption> problemOptionList = problemOptionService.listProblemId(problemId);
        List<ProblemCase> problemCases = problemCaseService.listByProblemId(problemId);
        List<MetaTag> tagList = metaTagService.listByBusinessId(problemId, OjTagBind.Problem);
        List<ProblemCodeTemplate> problemCodeTemplateList = problemCodeTemplateService.listByProblemId(problemId);

        List<ProblemOptionBO> problemOptionBOS = BeanCopyUtils.copyList(problemOptionList, ProblemOptionBO.class);
        List<ProblemCaseBO> problemCaseBos = BeanCopyUtils.copyList(problemCases, ProblemCaseBO.class);
        List<ProblemCodeTemplateCreateDTO> problemCodeTemplateCreateDataList = BeanCopyUtils.copyList(problemCodeTemplateList, ProblemCodeTemplateCreateDTO.class);

        // TODO 用例获取
        String path = FilePath.TESTCASE_BASE_FOLDER.buildPath(problemId);
        List<String> strings = AppUtils.isNotProd() ? List.of("/testcase_tmp/" + problemId) : FileUtil.listFileNames(path);

        ProblemCreateUpdateDTO problemCreateUpdateDTO = new ProblemCreateUpdateDTO();
        problemCreateUpdateDTO.setProblemBo(new ProblemBO(problem));

        problemCreateUpdateDTO.setOptions(problemOptionBOS);
        problemCreateUpdateDTO.setProblemCaseDataList(problemCaseBos);
        problemCreateUpdateDTO.setUploadTestcaseDir("");
        problemCreateUpdateDTO.setChangeModeCode(false);
        problemCreateUpdateDTO.setChangeJudgeCaseMode(false);
        problemCreateUpdateDTO.setLanguageIds(List.of());
        problemCreateUpdateDTO.setTagList(tagList);
        problemCreateUpdateDTO.setCodeTemplates(problemCodeTemplateCreateDataList);

        return problemCreateUpdateDTO;
    }

    @SneakyThrows
    @Override
    public void importExcel(ImportExcelDTO dto) {
        ExcelResult<ProblemImportDTO> excelResult = ExcelUtils.importExcel(dto.getFile().getInputStream(), ProblemImportDTO.class, true);
        List<ProblemImportDTO> list = excelResult.getList();
        List<String> errorList = excelResult.getErrorList();
        String analysis = excelResult.getAnalysis();
        System.out.println(" analysis : " + analysis);
        System.out.println(" isCover : " + dto.getIsCover());
    }

    @SneakyThrows
    @Override
    public void exportExcel(ProblemListDTO dto, HttpServletResponse response) {
        List<ProblemVO> list = list(dto);
        String fileName = "题目模板";
        OutputStream os = FileUtils.getOutputStream(response, fileName + ".xlsx");
        ExcelUtils.exportExcel(list, "题目", ProblemVO.class, os);
    }

    /**
     * 批量绑定题目测试用例
     * <p>
     * 该方法用于根据不同的测试用例类型，将多个题目测试用例与一个题目关联起来它首先会构建题目测试用例实体，
     * 然后根据题目的评判模式和测试用例类型计算总分，并初始化相应的测试测试用例
     *
     * @param problem                     题目实体，包含题目的基本信息
     * @param problemCaseDataList         题目测试用例的创建DTO列表，用于构建题目测试用例实体
     * @param problemCaseType             题目测试用例类型，决定如何处理测试测试用例
     * @param uploadTestcaseDir           上传的测试测试用例目录，用于存储测试测试用例文件
     * @param autoDeleteUploadTestcaseDir
     */
    @Override
    public void bindProblemCaseBatch(Problem problem, List<ProblemCaseBO> problemCaseDataList, ProblemCaseType problemCaseType, String uploadTestcaseDir,
                                     Boolean autoDeleteUploadTestcaseDir) {
        // 构建题目测试用例实体列表
        problemCaseService.saveCaseBatch(problem, problemCaseDataList, problemCaseType, uploadTestcaseDir, autoDeleteUploadTestcaseDir);
        // 更新题目信息
        mapper.update(problem);
    }

    @Override
    public List<Problem> listByIds(Collection<? extends Serializable> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return List.of();
        }
        return super.listByIds(ids);
    }
}