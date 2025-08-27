package ${dtoPkg};

import ${poPkg}.${poClassName};
import com.glowxq.core.common.entity.base.BaseDTO;
import com.glowxq.core.util.BeanCopyUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
<#list importPackages as pkg>
import ${pkg};
</#list>
<#if hasDateFormat == true>
import org.springframework.format.annotation.DateTimeFormat;
</#if>

/**
 * <p>
 * ${poClassName}修改DTO
 * </p>
 *
 * @author ${author}
 * @since ${datetime}
 */
@Data
@Schema(description = "${poClassName}修改DTO")
public class ${dtoUpdateClassName} implements BaseDTO {

<#list columns as field>
  <#if field.isPk =="1">
    @Schema(description =  "${field.columnComment}")
    private ${field.javaType} ${field.javaField};

  </#if>
  <#if field.isEdit == "1" && field.isPk == "0" >
    <#if field.javaType == "LocalDateTime">
    @Schema(description =  "${field.columnComment}")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private ${field.javaType} ${field.javaField};
    <#else>
    @Schema(description =  "${field.columnComment}")
    private ${field.javaType} ${field.javaField};
    </#if>

  </#if>
</#list>
    @Override
    public ${poClassName} buildEntity() {
        return BeanCopyUtils.copy(this, ${poClassName}.class);
    }
}