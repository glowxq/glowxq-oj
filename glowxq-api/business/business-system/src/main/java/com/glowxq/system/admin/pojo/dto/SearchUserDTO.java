package com.glowxq.system.admin.pojo.dto;

import com.glowxq.core.common.entity.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author glowxq
 * @version 1.0
 * @date 2025/6/24
 */
@Data
@Schema(description = "搜索用户DTO")
public class SearchUserDTO extends PageQuery {

    @Schema(description = "搜索关键词")
    private String searchKey;

}
