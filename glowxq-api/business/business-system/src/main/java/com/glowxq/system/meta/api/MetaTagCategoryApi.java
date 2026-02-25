package com.glowxq.system.meta.api;

import cn.dev33.satoken.annotation.SaIgnore;
import com.glowxq.core.common.api.BaseApi;
import com.glowxq.core.common.entity.ApiPageResult;
import com.glowxq.core.common.entity.ApiResult;
import com.glowxq.core.common.entity.PageResult;
import com.glowxq.system.meta.pojo.dto.MetaTagCategoryListDTO;
import com.glowxq.system.meta.pojo.vo.MetaTagCategoryVO;
import com.glowxq.system.meta.service.MetaTagCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * meta/标签分类 Api
 *
 * @author glowxq
 * @since 2025-06-10
 */
@Tag(name = "标签分类")
@SaIgnore
@RestController
@RequestMapping("/client")
@RequiredArgsConstructor
public class MetaTagCategoryApi extends BaseApi {

    private final MetaTagCategoryService metaTagCategoryService;

    @Operation(summary = "列表查询")
    @GetMapping("/meta-tag-category/list")
    public ApiResult<PageResult<MetaTagCategoryVO>> list(MetaTagCategoryListDTO dto) {
        return ApiPageResult.success(metaTagCategoryService.page(dto));
    }
}