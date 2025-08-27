package com.glowxq.oj.code.pojo.dto;

import com.glowxq.core.common.entity.base.BaseDTO;
import com.glowxq.core.util.BeanCopyUtils;
import com.glowxq.oj.code.pojo.po.CodeMonitor;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * <p>
 * CodeMonitor修改DTO
 * </p>
 *
 * @author glowxq
 * @since 2025-04-04
 */
@Data
@Schema(description = "CodeMonitor修改DTO")
public class CoveredCodeMonitorDTO implements BaseDTO {

    @Schema(description = "id")
    @NotNull
    private Long id;

    @Schema(description = "覆盖代码")
    @NotBlank
    @Length(min = 1, max = 3000)
    private String overlayCode;

    @Override
    public CodeMonitor buildEntity() {
        return BeanCopyUtils.copy(this, CodeMonitor.class);
    }
}