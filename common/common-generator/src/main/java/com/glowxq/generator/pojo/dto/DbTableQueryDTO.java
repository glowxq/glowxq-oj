package com.glowxq.generator.pojo.dto;

import com.glowxq.core.common.entity.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author glowxq
 * @since 2023/11/29 14:17
 */
@Schema(description = "导入表查询")
@Data
public class DbTableQueryDTO extends PageQuery {

    @Schema(description = "表名称")
    private String tableName;

    @Schema(description = "表描述")
    private String tableComment;

    @Schema(description = "是否过滤系统表")
    private boolean filterSys;

}
