package com.glowxq.oj.meta.pojo.po;

import com.glowxq.core.common.entity.base.BaseEntity;
import com.glowxq.mysql.EntityChangeListener;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.time.LocalDateTime;

/**
* <p>
* 语言
* </p>
*
* @author glowxq
* @since 2025-03-17
*/
@Data
@Table(value = "meta_language", onInsert = EntityChangeListener.class, onUpdate = EntityChangeListener.class)
@Schema(description = "语言")
public class MetaLanguage implements BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    @Schema(description ="主键")
    private Long id;

    @Schema(description ="语言类型")
    private String contentType;

    @Schema(description ="语言描述")
    private String description;

    @Schema(description ="语言名字")
    private String name;

    @Schema(description ="编译指令")
    private String compileCommand;

    @Schema(description ="模板")
    private String template;

    @Schema(description ="语言默认代码模板")
    private String codeTemplate;

    @Schema(description ="是否可作为特殊判题的一种语言")
    private Integer spjEnable;

    @Schema(description ="该语言属于哪个oj|自身oj用ME")
    private String oj;

    @Schema(description ="语言排序")
    private Integer seq;

    @Schema(description ="创建时间")
    private LocalDateTime createTime;

    @Schema(description ="更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "租户ID")
    private String tenantId;

    
    

    @Schema(description ="创建人ID")
    private Long createId;

    @Schema(description ="更新人ID")
    private Long updateId;

}