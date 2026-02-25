package com.glowxq.system.meta.api;

import cn.dev33.satoken.annotation.SaIgnore;
import com.glowxq.core.common.api.BaseApi;
import com.glowxq.core.common.entity.ApiPageResult;
import com.glowxq.core.common.entity.ApiResult;
import com.glowxq.core.common.entity.PageResult;
import com.glowxq.system.meta.pojo.dto.MetaCategoryListDTO;
import com.glowxq.system.meta.pojo.vo.MetaCategoryTreeVO;
import com.glowxq.system.meta.pojo.vo.MetaCategoryVO;
import com.glowxq.system.meta.service.MetaCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * meta/分类 Api
 *
 * @author glowxq
 * @since 2025-04-23
 */
@Tag(name = "分类")
@RestController
@SaIgnore
@RequestMapping("/client")
@RequiredArgsConstructor
public class MetaCategoryApi extends BaseApi {

    private final MetaCategoryService metaCategoryService;

    @Operation(summary = "列表查询")
    @GetMapping("/meta-category/list")
    public ApiResult<PageResult<MetaCategoryVO>> list(MetaCategoryListDTO dto) {
        return ApiPageResult.success(metaCategoryService.page(dto));
    }

    @Operation(summary = "树形列表")
    @GetMapping("/meta-category/tree")
    public ApiResult<List<MetaCategoryTreeVO>> tree(@Parameter(description = "需要排除的节点ID") @RequestParam(required = false) Integer excludeNodeId,
                                                    @Parameter(description = "是否添加根节点") @RequestParam(required = false) Boolean appendRoot) {
        return ApiResult.success(metaCategoryService.getTree(excludeNodeId, appendRoot, false));
    }
}