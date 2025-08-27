package com.glowxq.system.admin.pojo.vo.sysdept;

import com.glowxq.core.common.service.Treeable;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * DeptTreeVO
 * 
 * @author glowxq
 * @since 2024/3/22 9:59
 * @version 1.0
 */

@Data
public class DeptTreeVO implements Treeable<DeptTreeVO> {

    @Schema(description = "id")
    private Long id;

    @Schema(description = "pid")
    private Long pid;

    @Schema(description = "层级")
    private Long deep;

    @Schema(description = "排序")
    private Long sort;

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

    @Schema(description = "子级")
    private List<DeptTreeVO> children;

    @Schema(description = "name")
    private String name;

    @Schema(description = "用户数量")
    private Long userTotal = 0L;

}
