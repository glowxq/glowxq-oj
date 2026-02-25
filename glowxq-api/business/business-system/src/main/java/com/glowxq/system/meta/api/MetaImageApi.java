package com.glowxq.system.meta.api;

import cn.dev33.satoken.annotation.SaIgnore;
import com.glowxq.core.common.api.BaseApi;
import com.glowxq.core.common.entity.ApiPageResult;
import com.glowxq.core.common.entity.ApiResult;
import com.glowxq.core.common.entity.PageResult;
import com.glowxq.system.meta.enums.ImageBusinessType;
import com.glowxq.system.meta.pojo.dto.MetaImageListDTO;
import com.glowxq.system.meta.pojo.po.MetaImage;
import com.glowxq.system.meta.pojo.vo.MetaImageVO;
import com.glowxq.system.meta.service.MetaImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * meta/图片 Api
 *
 * @author glowxq
 * @since 2025-04-25
 */
@Tag(name = "图片")
@RestController
@RequestMapping("/client")
@SaIgnore
@RequiredArgsConstructor
public class MetaImageApi extends BaseApi {

    private final MetaImageService metaImageService;

    @Operation(summary = "列表查询")
    @GetMapping("/meta-image/list")
    public ApiResult<PageResult<MetaImageVO>> list(MetaImageListDTO dto) {
        return ApiPageResult.success(metaImageService.page(dto));
    }

    @Operation(summary = "根据key获取")
    @GetMapping("/meta-image/getByKey")
    public ApiResult<MetaImage> getByKey(@RequestParam String key) {
        return ApiResult.success(metaImageService.getByKey(key));
    }

    @Operation(summary = "根据图片类型获取")
    @GetMapping("/meta-image/listByType")
    public ApiResult<List<MetaImage>> listByType(@RequestParam ImageBusinessType imageBusinessType) {
        return ApiResult.success(metaImageService.listByType(imageBusinessType));
    }
}