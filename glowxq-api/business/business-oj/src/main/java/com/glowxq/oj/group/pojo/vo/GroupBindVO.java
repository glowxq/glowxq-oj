package com.glowxq.oj.group.pojo.vo;

import cn.idev.excel.annotation.ExcelIgnore;
import cn.idev.excel.annotation.ExcelProperty;
import com.glowxq.core.common.entity.base.BaseVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * <p>
 * GroupBind返回vo
 * </p>
 *
 * @author glowxq
 * @since 2025-03-28
 */
@Data
@Schema(description = "GroupBind返回vo")
public class GroupBindVO implements BaseVO{

    @ExcelIgnore
    @Schema(description =  "")
    private Long id;

    @ExcelProperty(value = "班级ID")
    @Schema(description =  "班级ID")
    private Long groupId;

    @ExcelProperty(value = "班级绑定的业务ID")
    @Schema(description =  "班级绑定的业务ID")
    private Long businessId;

    @ExcelProperty(value = "绑定类型")
    @Schema(description =  "绑定类型")
    private String type;

}