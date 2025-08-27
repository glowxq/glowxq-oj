package ${controllerPkg};

<#compress>
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
<#if GeneratorInfo.hasImport == "1">
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
</#if>
import lombok.RequiredArgsConstructor;
import cn.dev33.satoken.annotation.SaCheckPermission;
import org.springframework.web.bind.annotation.*;
import com.glowxq.core.common.entity.ApiPageResult;
import com.glowxq.core.common.entity.ApiResult;
import com.glowxq.core.common.constant.GlobalConstant;

import com.glowxq.core.common.api.BaseApi;
import com.glowxq.core.common.entity.PageResult;
import com.glowxq.core.common.entity.SelectIdsDTO;
import ${servicePkg}.${serviceClassName};
import ${dtoPkg}.${dtoCreateClassName};
import ${dtoPkg}.${dtoUpdateClassName};
import ${dtoPkg}.${dtoListClassName};
import ${voPkg}.${voClassName};
<#if GeneratorInfo.hasImport == "1">
import com.glowxq.core.common.entity.ImportExcelDTO;
</#if>
<#if GeneratorInfo.hasExport == "1">
import jakarta.servlet.http.HttpServletResponse;
</#if>
</#compress>


/**
 * ${moduleName}/${tableComment} Api
 *
 * @author ${author}
 * @since ${datetime}
 */
@Tag(name =  "${tableComment}")
@RestController
@RequestMapping
@RequiredArgsConstructor
public class ${controllerClassName} extends BaseApi  {

<#assign serviceName = lower_case_first_letter(serviceClassName)>
    private final ${serviceClassName} ${serviceName};

    @Operation(summary = "新增")
<#if GeneratorInfo.btnPermissionType == "1">
    @SaCheckPermission(value = "${createPermission}")
</#if>
    @PostMapping("/${router}/create")
    public ApiResult<Void> create(@RequestBody ${dtoCreateClassName} dto) {
        ${serviceName}.create(dto);
        return ApiResult.success();
    }

    @Operation(summary = "修改")
<#if GeneratorInfo.btnPermissionType == "1">
    @SaCheckPermission(value = "${updatePermission}")
</#if>
    @PutMapping("/${router}/update")
    public ApiResult<Void> update(@RequestBody ${dtoUpdateClassName} dto) {
        ${serviceName}.update(dto);
        return ApiResult.success();
    }

    @Operation(summary = "删除")
<#if GeneratorInfo.btnPermissionType == "1">
    @SaCheckPermission(value = "${removePermission}")
</#if>
    @DeleteMapping("/${router}/remove")
    public ApiResult<Void> remove(@RequestBody SelectIdsDTO dto) {
        ${serviceName}.remove(dto);
        return ApiResult.success();
    }

    @Operation(summary = "列表查询")
    @SaCheckPermission(value = "${listPermission}")
    @GetMapping("/${router}/list")
    public ApiResult<PageResult<${voClassName}>> list(${dtoListClassName} dto) {
        return ApiPageResult.success(${serviceName}.page(dto));
    }

    @Operation(summary = "详情")
    @SaCheckPermission(value = "${listPermission}")
    @GetMapping("/${router}/detail")
    public ApiResult<${voClassName}> detail(@RequestParam Long id) {
        return ApiResult.success(${serviceName}.detail(id));
    }
<#if GeneratorInfo.hasImport == "1">

    @Operation(summary = "导入")
    @Parameters({
      @Parameter(name = "file", description = "上传文件", schema = @Schema(type = "string", format = "binary"), required = true),
    })
    <#if GeneratorInfo.btnPermissionType == "1">
    @SaCheckPermission(value = "${importPermission}")
    </#if>
    @PostMapping("/${router}/import")
    public void importExcel(@ModelAttribute ImportExcelDTO dto) {
        ${serviceName}.importExcel(dto);
    }
</#if>
<#if GeneratorInfo.hasExport == "1">

    @Operation(summary = "导出")
  <#if GeneratorInfo.btnPermissionType == "1">
    @SaCheckPermission(value = "${exportPermission}")
  </#if>
    @PostMapping("/${router}/export")
    public void exportExcel(@RequestBody ${dtoListClassName} dto, HttpServletResponse response) {
        ${serviceName}.exportExcel(dto, response);
    }
</#if>
<#compress>
}
<#-- 内建函数，实现首字母小写 -->
<#function lower_case_first_letter str>
    <#return str?substring(0, 1)?lower_case + str?substring(1)>
</#function>
</#compress>