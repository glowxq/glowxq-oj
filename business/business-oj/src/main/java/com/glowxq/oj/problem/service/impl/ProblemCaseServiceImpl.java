package com.glowxq.oj.problem.service.impl;

import com.glowxq.core.common.constant.Constant;
import com.glowxq.core.common.entity.ImportExcelDTO;
import com.glowxq.core.common.entity.PageResult;
import com.glowxq.core.common.entity.SelectIdsDTO;
import com.glowxq.core.common.enums.CommonResponseEnum;
import com.glowxq.core.util.*;
import com.glowxq.excel.core.ExcelResult;
import com.glowxq.excel.utils.ExcelUtils;
import com.glowxq.oj.judge.enums.ContestType;
import com.glowxq.oj.problem.enums.FilePath;
import com.glowxq.oj.problem.enums.ProblemCaseType;
import com.glowxq.oj.problem.mapper.ProblemCaseMapper;
import com.glowxq.oj.problem.pojo.dto.ProblemCaseBO;
import com.glowxq.oj.problem.pojo.dto.ProblemCaseImportDTO;
import com.glowxq.oj.problem.pojo.dto.ProblemCaseListDTO;
import com.glowxq.oj.problem.pojo.dto.ProblemCaseUpdateDTO;
import com.glowxq.oj.problem.pojo.po.Problem;
import com.glowxq.oj.problem.pojo.po.ProblemCase;
import com.glowxq.oj.problem.pojo.vo.ProblemCaseVO;
import com.glowxq.oj.problem.service.ProblemCaseService;
import com.glowxq.oj.problem.utils.ProblemCaseUtils;
import com.glowxq.oss.OssClient;
import com.glowxq.oss.UploadResult;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 测试用例 服务实现类
 * </p>
 *
 * @author glowxq
 * @since 2025-03-17
 */
@Service
@RequiredArgsConstructor
public class ProblemCaseServiceImpl extends ServiceImpl<ProblemCaseMapper, ProblemCase> implements ProblemCaseService {

    private final OssClient ossClient;

    private static QueryWrapper buildQueryWrapper(ProblemCaseListDTO dto) {
        QueryWrapper wrapper = QueryWrapper.create().from(ProblemCase.class);
        wrapper.eq(ProblemCase::getProblemId, dto.getProblemId(), Utils.isNotNull(dto.getProblemId()));
        wrapper.eq(ProblemCase::getProblemKey, dto.getProblemKey(), Utils.isNotNull(dto.getProblemKey()));
        wrapper.eq(ProblemCase::getInput, dto.getInput(), Utils.isNotNull(dto.getInput()));
        wrapper.eq(ProblemCase::getOutput, dto.getOutput(), Utils.isNotNull(dto.getOutput()));
        wrapper.eq(ProblemCase::getInputUrl, dto.getInputUrl(), Utils.isNotNull(dto.getInputUrl()));
        wrapper.eq(ProblemCase::getOutputUrl, dto.getOutputUrl(), Utils.isNotNull(dto.getOutputUrl()));
        wrapper.eq(ProblemCase::getType, dto.getType(), Utils.isNotNull(dto.getType()));
        wrapper.eq(ProblemCase::getScore, dto.getScore(), Utils.isNotNull(dto.getScore()));
        wrapper.eq(ProblemCase::getEnable, dto.getEnable(), Utils.isNotNull(dto.getEnable()));
        wrapper.eq(ProblemCase::getGroupNum, dto.getGroupNum(), Utils.isNotNull(dto.getGroupNum()));
        return wrapper;
    }

    @Override
    public void create(ProblemCaseBO dto) {
        ProblemCase problemCase = BeanCopyUtils.copy(dto, ProblemCase.class);
        save(problemCase);
    }

    @Override
    public void update(ProblemCaseUpdateDTO dto) {
        ProblemCase problemCase = BeanCopyUtils.copy(dto, ProblemCase.class);
        QueryWrapper wrapper;
        // id有效性校验
        wrapper = QueryWrapper.create()
                              .eq(ProblemCase::getId, dto.getId());
        CommonResponseEnum.INVALID_ID.assertTrue(count(wrapper) <= 0);

        saveOrUpdate(problemCase);
    }

    @Override
    public PageResult<ProblemCaseVO> page(ProblemCaseListDTO dto) {
        Page<ProblemCaseVO> page = pageAs(PageUtils.getPage(dto), buildQueryWrapper(dto), ProblemCaseVO.class);
        return PageUtils.getPageResult(page);
    }

    @Override
    public List<ProblemCaseVO> list(ProblemCaseListDTO dto) {
        return listAs(buildQueryWrapper(dto), ProblemCaseVO.class);
    }

    @Override
    public void remove(SelectIdsDTO dto) {
        CommonResponseEnum.INVALID_ID.assertTrue(dto.getIds().isEmpty());
        removeByIds(dto.getIds());
    }

    @Override
    public ProblemCaseVO detail(Object id) {
        ProblemCase problemCase = getById((Serializable) id);
        CommonResponseEnum.INVALID_ID.assertNull(problemCase);
        return BeanCopyUtils.copy(problemCase, ProblemCaseVO.class);
    }

    @SneakyThrows
    @Override
    public void importExcel(ImportExcelDTO dto) {
        ExcelResult<ProblemCaseImportDTO> excelResult = ExcelUtils.importExcel(dto.getFile().getInputStream(), ProblemCaseImportDTO.class, true);
        List<ProblemCaseImportDTO> list = excelResult.getList();
        List<String> errorList = excelResult.getErrorList();
        String analysis = excelResult.getAnalysis();
        System.out.println(" analysis : " + analysis);
        System.out.println(" isCover : " + dto.getIsCover());
    }

    @SneakyThrows
    @Override
    public void exportExcel(ProblemCaseListDTO dto, HttpServletResponse response) {
        List<ProblemCaseVO> list = list(dto);
        String fileName = "测试用例模板";
        OutputStream os = FileUtils.getOutputStream(response, fileName + ".xlsx");
        ExcelUtils.exportExcel(list, "测试用例", ProblemCaseVO.class, os);
    }

    @Override
    public List<ProblemCase> listByProblemId(Long problemId) {
        QueryWrapper qw = QueryWrapper.create().from(ProblemCase.class).eq(ProblemCase::getProblemId, problemId);
        return mapper.selectListByQuery(qw);
    }

    @Override
    public List<ProblemCase> saveCaseBatch(Problem problem, List<ProblemCaseBO> problemCaseDataList, ProblemCaseType problemCaseType, String uploadTestcaseDir,
                                           Boolean autoDeleteUploadTestcaseDir) {
        List<ProblemCase> problemCaseList = problemCaseDataList.stream().map(sample -> {
            ProblemCase problemCase = sample.buildEntity();
            problemCase.setProblemId(problem.getId());
            problemCase.setProblemKey(problem.getProblemKey());
            problemCase.setInputUrl("");
            problemCase.setOutputUrl("");
            return problemCase;
        }).toList();

        String testCasesDir = FilePath.TESTCASE_BASE_FOLDER.buildPath(problem.getId());
        // 根据题目测试用例类型处理测试测试用例
        switch (problemCaseType) {
            case ManualEditing -> {
                // 如果是OI模式，计算总分
                if (problem.getProgramType().equals(ContestType.TYPE_OI.getCode())) {
                    Integer sumScore = ProblemCaseUtils.calProblemTotalScore(problem.getJudgeCaseMode(), problemCaseList);
                    problem.setIoScore(sumScore);
                }
                // 处理手写case
                ProblemCaseUtils.initHandTestCase(problem.getJudgeMode(), problem.getJudgeCaseMode(), problem.getCaseVersion(), problem.getId(), problemCaseList);
            }
            case FileUpload -> {
                // 如果是OI模式，计算总分
                if (problem.getProgramType().equals(ContestType.TYPE_OI.getCode())) {
                    int sumScore = problemCaseDataList.stream().mapToInt(ProblemCaseBO::getScore).sum();
                    problem.setIoScore(sumScore);
                }
                // 处理之前上传的case
                ProblemCaseUtils.initUploadTestCase(
                        autoDeleteUploadTestcaseDir,
                        problem.getJudgeMode(),
                        problem.getJudgeCaseMode(),
                        problem.getCaseVersion(),
                        uploadTestcaseDir,
                        testCasesDir,
                        problemCaseList);
            }
        }

        problemCaseList = problemCaseList.stream().peek(problemCase -> {
            String inputPath = testCasesDir + File.separator + problemCase.getInput();
            String outputPath = testCasesDir + File.separator + problemCase.getOutput();
            File inputFile = new File(inputPath);
            File outputFile = new File(outputPath);

            String fileKeyPathPrefix = FilePath.TESTCASE_BASE_FOLDER.buildUrlPath(problem.getId()) + Constant.URL_SEPARATOR;
            UploadResult inputUploadResult = ossClient.uploadFile(inputFile, fileKeyPathPrefix + problemCase.getInput());
            UploadResult outputUploadResult = ossClient.uploadFile(outputFile, fileKeyPathPrefix + problemCase.getOutput());
            problemCase.setInputUrl(inputUploadResult.getUrl());
            problemCase.setOutputUrl(outputUploadResult.getUrl());
        }).toList();
        mapper.insertBatch(problemCaseList);

        return problemCaseList;
    }
}