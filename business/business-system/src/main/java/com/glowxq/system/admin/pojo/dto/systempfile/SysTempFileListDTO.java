package com.glowxq.system.admin.pojo.dto.systempfile;

import com.glowxq.core.common.entity.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
/**
 * <p>
 * SysTempFile查询DTO
 * </p>
 *
 * @author glowxq-admin
 * @since 2024-12-05
 */
@Data
@Schema(description = "SysTempFile查询DTO")
public class SysTempFileListDTO extends PageQuery {

    @Schema(description = "模版名")
    private String tempName;

}