package com.glowxq.oj.meta.controller;

import com.glowxq.core.common.entity.ApiPageResult;
import com.glowxq.core.common.entity.ApiResult;
import com.glowxq.core.common.entity.PageResult;
import com.glowxq.oj.meta.pojo.dto.MetaLanguageListDTO;
import com.glowxq.oj.meta.pojo.vo.MetaLanguageVO;
import com.glowxq.oj.meta.service.MetaLanguageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * group/班级表 Api
 *
 * @author glowxq
 * @since 2025-03-27
 */
@Tag(name = "语言")
@RestController
@RequestMapping("/meta-language")
@RequiredArgsConstructor
public class MetaLanguageController {

    private final MetaLanguageService metaLanguageService;

    @Operation(summary = "列表查询")
    @GetMapping
    public ApiResult<PageResult<MetaLanguageVO>> list(MetaLanguageListDTO dto) {
        return ApiPageResult.success(metaLanguageService.page(dto));
    }
}
