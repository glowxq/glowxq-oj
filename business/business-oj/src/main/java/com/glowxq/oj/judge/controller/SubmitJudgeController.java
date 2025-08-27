package com.glowxq.oj.judge.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import com.glowxq.core.common.entity.ApiResult;
import com.glowxq.oj.judge.pojo.po.Judge;
import com.glowxq.oj.submit.SubmitInterface;
import com.glowxq.oj.submit.compile.bo.CompileSubmitDTO;
import com.glowxq.oj.submit.normal.dto.GlobalNormalSubmitDTO;
import com.glowxq.oj.submit.test.TestSubmitDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测评记录 Controller
 *
 * @author glowxq
 * @since 2025-03-17
 */
@Tag(name = "测评记录")
@RestController
@RequestMapping("/judge/submit")
@RequiredArgsConstructor
@SaIgnore
public class SubmitJudgeController {

    private final SubmitInterface submitInterface;

    @Operation(summary = "普通测评")
    // @SaCheckPermission(value = "judge.submit.normal", orRole = Role.STUDENT, mode = SaMode.OR)
    @PostMapping("/submitNormal")
    public ApiResult<Judge> submitNormal(@RequestBody GlobalNormalSubmitDTO dto) {
        Judge judge = submitInterface.submitNormal(dto);
        return ApiResult.success(judge);
    }

    @Operation(summary = "测试测评(在线调试)")
    // @SaCheckPermission(value = "judge.submit.normal", orRole = Role.STUDENT, mode = SaMode.OR)
    @PostMapping("/submitTest")
    public ApiResult<Void> submitTest(@RequestBody TestSubmitDTO dto) {
        submitInterface.submitTest(dto);
        return ApiResult.success();
    }

    @Operation(summary = "编译测评")
    // @SaCheckPermission(value = "judge.submit.compile", orRole = Role.STUDENT, mode = SaMode.OR)
    @PostMapping("/submitCompile")
    public ApiResult<Void> submitCompile(@RequestBody CompileSubmitDTO dto) {
        submitInterface.submitCompile(dto);
        return ApiResult.success();
    }
}