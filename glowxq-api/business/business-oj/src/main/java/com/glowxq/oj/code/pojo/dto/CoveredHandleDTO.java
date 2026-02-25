package com.glowxq.oj.code.pojo.dto;

import com.glowxq.core.common.entity.base.BaseDTO;
import com.glowxq.core.common.valid.annotation.InEnum;
import com.glowxq.core.util.BeanCopyUtils;
import com.glowxq.oj.code.enums.CodeMonitorStatus;
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
public class CoveredHandleDTO implements BaseDTO {

    @Schema(description = "id")
    @NotNull
    private Long id;

    @Schema(description = "状态接收或拒绝")
    @NotNull
    @NotBlank
    @InEnum(enumClass = CodeMonitorStatus.class)
    @Length(min = 1, max = 3000)
    private String monitorStatus;

    @Override
    public CodeMonitor buildEntity() {
        return BeanCopyUtils.copy(this, CodeMonitor.class);
    }
}