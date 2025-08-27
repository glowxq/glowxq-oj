package com.glowxq.oj.code.pojo.dto;

import cn.idev.excel.annotation.ExcelProperty;
import com.glowxq.core.common.entity.base.BaseDTO;
import com.glowxq.core.util.BeanCopyUtils;
import com.glowxq.oj.code.pojo.po.CodeRecord;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * <p>
 * CodeRecord导入DTO
 * </p>
 *
 * @author glowxq
 * @since 2025-04-04
 */
@Data
@Schema(description = "CodeRecord导入DTO")
public class CodeRecordImportDTO implements BaseDTO {

    @ExcelProperty(value = "用户id")
    @Schema(description =  "用户id")
    private Long userId;

    @ExcelProperty(value = "姓名")
    @Schema(description =  "姓名")
    private String name;

    @ExcelProperty(value = "用户名")
    @Schema(description =  "用户名")
    private String username;

    @ExcelProperty(value = "代码")
    @Schema(description =  "代码")
    private String code;

    @ExcelProperty(value = "代码模式")
    @Schema(description =  "代码模式")
    private String codeMode;

    @Override
    public CodeRecord buildEntity() {
        return BeanCopyUtils.copy(this, CodeRecord.class);
    }
}