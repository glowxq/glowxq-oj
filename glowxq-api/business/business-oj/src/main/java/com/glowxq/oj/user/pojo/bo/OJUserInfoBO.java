package com.glowxq.oj.user.pojo.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author glowxq
 * @version 1.0
 * @date 2025/4/9
 */
@Data
public class OJUserInfoBO {

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "姓名")
    private String name;

    @Schema(description = "ac数量")
    private Integer acNum;

    @Schema(description = "用户积分")
    private Integer integral;

    @Schema(description = "连续签到时间")
    private Integer continuousSignDay;

    @Schema(description = "提交题目数量")
    private Integer submitNum;

    @Schema(description = "称号")
    private String title;

    @Schema(description = "颜色")
    private String color;

    @Schema(description = "租户ID")
    private String tenantId;

}
