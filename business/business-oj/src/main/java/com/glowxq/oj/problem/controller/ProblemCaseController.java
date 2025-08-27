package com.glowxq.oj.problem.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import com.glowxq.core.common.annotation.OperationLog;
import com.glowxq.core.common.api.BaseApi;
import com.glowxq.core.common.entity.ApiResult;
import com.glowxq.core.common.enums.ModuleEnum;
import com.glowxq.core.util.FileUtils;
import com.glowxq.oj.problem.pojo.vo.TestCaseUploadResult;
import com.glowxq.oj.problem.service.ProblemCaseService;
import com.glowxq.oj.problem.utils.ProblemCaseUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>
 * 测试用例 Controller
 * </p>
 *
 * @author glowxq
 * @since 2025-03-17
 */
@Tag(name = "测试用例")
@RestController
@RequestMapping("problem-case")
@RequiredArgsConstructor
public class ProblemCaseController extends BaseApi {

    private final ProblemCaseService problemCaseService;

    /**
     * 上传测试用例文件
     *
     * @param file
     * @param mode
     * @return
     */
    @SaIgnore
    @Operation(summary = "上传测试用例文件")
    @PostMapping("/case/uploadTestcaseZip")
    @OperationLog(module = ModuleEnum.Judge)
    public ApiResult<TestCaseUploadResult> uploadTestcaseZip(@RequestParam("file") MultipartFile file,
                                                             @RequestParam(value = "mode", defaultValue = "default") String mode) {
        TestCaseUploadResult testCaseUploadResult = ProblemCaseUtils.uploadTestcaseZip(file, mode);
        return ApiResult.success(testCaseUploadResult);
    }

    /**
     * 上传测试用例文件
     *
     * @param url
     * @param mode
     * @return
     */
    @SaIgnore
    @Operation(summary = "上传测试用例文件")
    @PostMapping("/case/uploadTestcaseZipUrl")
    @OperationLog(module = ModuleEnum.Judge)
    public ApiResult<TestCaseUploadResult> uploadTestcaseZipUrl(@RequestParam("url") String url,
                                                                @RequestParam(value = "mode", defaultValue = "default") String mode) {
        MultipartFile file = FileUtils.downloadFile(url);
        TestCaseUploadResult testCaseUploadResult = ProblemCaseUtils.uploadTestcaseZip(file, mode);
        return ApiResult.success(testCaseUploadResult);
    }
}