package com.glowxq.system.admin.pojo.dto.sysdept;

import com.glowxq.core.common.entity.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * <p>
 * SysDept添加DTO
 * </p>
 *
 * @author glowxq
 * @since 2024-03-20
 */
@Data
@Schema(description = "SysDept查询DTO")
public class SysDeptListDTO extends PageQuery {

    @Schema(description = "部门名称")
    private String name;

    @Schema(description = "所属地址区域")
    private Long regionId;

    @Schema(description = "编号前缀")
    private String numberPrefix;

    @Schema(description = "部门编号")
    private String deptNumber;

    @Schema(description = "部门logo")
    private String logo;

    @Schema(description = "部门图片")
    private String image;

    @Schema(description = "部门介绍")
    private String content;

}