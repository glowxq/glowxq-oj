package com.glowxq.oj.code.pojo.dto;

import com.glowxq.core.common.entity.PageQuery;
import com.glowxq.core.common.entity.base.BaseDTO;
import com.glowxq.core.util.BeanCopyUtils;
import com.glowxq.oj.code.pojo.po.CodeMonitor;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * <p>
 * CodeMonitor查询DTO
 * </p>
 *
 * @author glowxq
 * @since 2025-04-04
 */
@Data
@Schema(description = "SearchMonitorCodeDTO查询DTO")
public class SearchMonitorCodeDTO extends PageQuery implements BaseDTO {

    @Schema(description = "搜索关键字")
    private String searchKey;

    @Schema(description = "被监控用户id")
    private Long monitorUserId;

    @Override
    public CodeMonitor buildEntity() {
        return BeanCopyUtils.copy(this, CodeMonitor.class);
    }
}