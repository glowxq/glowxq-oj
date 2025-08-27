package com.glowxq.oj.code.pojo.dto;

import com.glowxq.core.common.entity.PageQuery;
import com.glowxq.core.common.entity.base.BaseDTO;
import com.glowxq.core.util.BeanCopyUtils;
import com.glowxq.oj.code.pojo.po.CodeRecord;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * <p>
 * CodeRecord查询DTO
 * </p>
 *
 * @author glowxq
 * @since 2025-04-04
 */
@Data
@Schema(description = "CodeRecord查询DTO")
public class CodeRecordListDTO extends PageQuery implements BaseDTO {

    @Schema(description = "用户id")
    private Long userId;

    @Schema(description = "姓名")
    private String name;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "代码")
    private String code;

    @Schema(description = "代码模式")
    private String codeMode;



    @Override
    public CodeRecord buildEntity() {
        return BeanCopyUtils.copy(this, CodeRecord.class);
    }
}