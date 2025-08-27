package com.glowxq.system.meta.api;

import cn.dev33.satoken.annotation.SaIgnore;
import com.glowxq.core.common.api.BaseApi;
import com.glowxq.core.common.entity.ApiPageResult;
import com.glowxq.core.common.entity.ApiResult;
import com.glowxq.core.common.entity.PageResult;
import com.glowxq.system.meta.pojo.dto.MetaMenuListDTO;
import com.glowxq.system.meta.pojo.vo.MetaMenuVO;
import com.glowxq.system.meta.service.MetaMenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * meta/菜单 Api
 *
 * @author glowxq
 * @since 2025-04-23
 */
@Tag(name =  "菜单")
@RestController
@SaIgnore
@RequestMapping("/client")
@RequiredArgsConstructor
public class MetaMenuApi extends BaseApi  {

    private final MetaMenuService metaMenuService;


    @Operation(summary = "列表查询")
    @GetMapping("/meta-menu/list")
    public ApiResult<PageResult<MetaMenuVO>> list(MetaMenuListDTO dto) {
        return ApiPageResult.success(metaMenuService.page(dto));
    }
}