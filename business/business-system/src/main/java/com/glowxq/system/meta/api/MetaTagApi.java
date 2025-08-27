package com.glowxq.system.meta.api;

import cn.dev33.satoken.annotation.SaIgnore;
import com.glowxq.core.common.api.BaseApi;
import com.glowxq.core.common.entity.ApiPageResult;
import com.glowxq.core.common.entity.ApiResult;
import com.glowxq.core.common.entity.PageResult;
import com.glowxq.system.meta.pojo.dto.MetaTagListDTO;
import com.glowxq.system.meta.pojo.vo.MetaTagVO;
import com.glowxq.system.meta.service.MetaTagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * meta/标签 Api
 *
 * @author glowxq
 * @since 2025-04-23
 */
@Tag(name = "标签")
@RestController
@SaIgnore
@RequestMapping("/client")
@RequiredArgsConstructor
public class MetaTagApi extends BaseApi {

    private final MetaTagService metaTagService;

    @Operation(summary = "列表查询")
    @GetMapping("/meta-tag/list")
    public ApiResult<PageResult<MetaTagVO>> list(MetaTagListDTO dto) {
        return ApiPageResult.success(metaTagService.list(dto));
    }
}