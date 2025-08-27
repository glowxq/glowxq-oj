package com.glowxq.oj.code.pojo.dto;

import com.glowxq.core.common.entity.base.BaseDTO;
import com.glowxq.core.common.valid.annotation.InEnum;
import com.glowxq.core.util.BeanCopyUtils;
import com.glowxq.oj.code.enums.CodeMode;
import com.glowxq.oj.code.pojo.po.CodeMonitor;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * <p>
 * CodeMonitor添加DTO
 * </p>
 *
 * @author glowxq
 * @since 2025-04-04
 */
@Data
@Schema(description = "CodeMonitor添加DTO")
public class CodeMonitorDTO implements BaseDTO {

    @Schema(description = "id")
    private Long id;

    @Schema(description = "被监控代码")
    @NotNull
    @NotBlank
    @Length(min = 1, max = 3000)
    private String monitorCode;

    @Schema(description = "代码模式")
    @InEnum(enumClass = CodeMode.class)
    @NotBlank
    @NotNull
    private String codeMode;

    @Override
    public CodeMonitor buildEntity() {
        return BeanCopyUtils.copy(this, CodeMonitor.class);
    }
}