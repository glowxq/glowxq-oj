package com.glowxq.oj.code.pojo.po;

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
* 用户代码
* </p>
*
* @author glowxq
* @since 2025-04-04
*/
@Data
@Table(value = "code_record", onInsert = EntityChangeListener.class, onUpdate = EntityChangeListener.class)
@Schema(description = "用户代码")
public class CodeRecord implements BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    @Schema(description ="")
    private Long id;

    @Schema(description ="用户id")
    private Long userId;

    @Schema(description ="姓名")
    private String name;

    @Schema(description ="用户名")
    private String username;

    @Schema(description ="代码")
    private String code;

    @Schema(description ="代码模式")
    private String codeMode;

    @Schema(description ="创建时间")
    private LocalDateTime createTime;

    @Schema(description ="更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "租户ID")
    private String tenantId;

    @Schema(description ="是否删除")
    private String delFlag;

    @Schema(description ="创建人ID")
    private Long createId;

    @Schema(description ="更新人ID")
    private Long updateId;

}