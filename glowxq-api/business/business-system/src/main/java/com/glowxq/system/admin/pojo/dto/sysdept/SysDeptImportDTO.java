package com.glowxq.system.admin.pojo.dto.sysdept;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * <p>
 * SysDept导入DTO
 * </p>
 *
 * @author glowxq
 * @since 2024-03-20
 */
@Data
@Schema(description = "SysDept导入DTO")
public class SysDeptImportDTO {

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