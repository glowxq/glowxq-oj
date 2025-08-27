package com.glowxq.system.meta.api;

import cn.dev33.satoken.annotation.SaIgnore;
import com.glowxq.core.common.api.BaseApi;
import com.glowxq.core.common.entity.ApiPageResult;
import com.glowxq.core.common.entity.ApiResult;
import com.glowxq.core.common.entity.PageResult;
import com.glowxq.system.meta.pojo.dto.MetaTextListDTO;
import com.glowxq.system.meta.pojo.po.MetaText;
import com.glowxq.system.meta.pojo.vo.MetaTextVO;
import com.glowxq.system.meta.service.MetaTextService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * meta/文本 Api
 *
 * @author glowxq
 * @since 2025-04-23
 */
@Tag(name = "文本")
@RestController
@SaIgnore
@RequestMapping("/client")
@RequiredArgsConstructor
public class MetaTextApi extends BaseApi {

    private final MetaTextService metaTextService;

    @Operation(summary = "列表查询")
    @GetMapping("/meta-text/list")
    public ApiResult<PageResult<MetaTextVO>> list(MetaTextListDTO dto) {
        return ApiPageResult.success(metaTextService.list(dto));
    }

    @Operation(summary = "根据key获取")
    @GetMapping("/meta-text/getByKey")
    public ApiResult<MetaText> getByKey(@RequestParam String key) {
        return ApiResult.success(metaTextService.getByKey(key));
    }
}