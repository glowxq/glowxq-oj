package com.glowxq.system.admin.pojo.po;

import com.glowxq.core.common.entity.base.BaseEntity;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
/**
 * <p>
 * 部门祖籍关系表
 * </p>
 *
 * @author glowxq
 * @since 2024-03-28
 */
@Data
@Table(value = "sys_dept_closure")
@Schema(description = "部门祖籍关系表")
public class SysDeptClosure implements BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Schema(description = "祖先节点ID")
    private Long ancestorId;

    @Schema(description = "后代节点ID")
    private Long descendantId;

    @Schema(description = "祖先节点到后代节点的距离")
    private Integer depth;

}