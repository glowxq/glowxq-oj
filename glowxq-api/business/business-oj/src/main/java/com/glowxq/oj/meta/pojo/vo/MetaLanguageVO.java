package com.glowxq.oj.meta.pojo.vo;

import cn.idev.excel.annotation.ExcelIgnore;
import cn.idev.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * <p>
 * MetaLanguage返回vo
 * </p>
 *
 * @author glowxq
 * @since 2025-03-17
 */
@Data
@Schema(description = "MetaLanguage返回vo")
public class MetaLanguageVO {

    @ExcelIgnore
    @Schema(description =  "主键")
    private String id;

    @ExcelProperty(value = "语言类型")
    @Schema(description =  "语言类型")
    private String contentType;

    @ExcelProperty(value = "语言描述")
    @Schema(description =  "语言描述")
    private String description;

    @ExcelProperty(value = "语言名字")
    @Schema(description =  "语言名字")
    private String name;

    @ExcelProperty(value = "编译指令")
    @Schema(description =  "编译指令")
    private String compileCommand;

    @ExcelProperty(value = "模板")
    @Schema(description =  "模板")
    private String template;

    @ExcelProperty(value = "语言默认代码模板")
    @Schema(description =  "语言默认代码模板")
    private String codeTemplate;

    @ExcelProperty(value = "是否可作为特殊判题的一种语言")
    @Schema(description =  "是否可作为特殊判题的一种语言")
    private Integer spjEnable;

    @ExcelProperty(value = "该语言属于哪个oj|自身oj用ME")
    @Schema(description =  "该语言属于哪个oj|自身oj用ME")
    private String oj;

    @ExcelProperty(value = "语言排序")
    @Schema(description =  "语言排序")
    private Integer seq;

}