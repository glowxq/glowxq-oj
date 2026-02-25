package com.glowxq.oj.meta.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * <p>
 * MetaLanguage修改DTO
 * </p>
 *
 * @author glowxq
 * @since 2025-03-17
 */
@Data
@Schema(description = "MetaLanguage修改DTO")
public class MetaLanguageUpdateDTO {

    @Schema(description =  "主键")
    private String id;

    @Schema(description =  "语言类型")
    private String contentType;

    @Schema(description =  "语言描述")
    private String description;

    @Schema(description =  "语言名字")
    private String name;

    @Schema(description =  "编译指令")
    private String compileCommand;

    @Schema(description =  "模板")
    private String template;

    @Schema(description =  "语言默认代码模板")
    private String codeTemplate;

    @Schema(description =  "是否可作为特殊判题的一种语言")
    private Integer spjEnable;

    @Schema(description =  "该语言属于哪个oj|自身oj用ME")
    private String oj;

    @Schema(description =  "语言排序")
    private Integer seq;

}