package com.glowxq.system.admin.pojo.po;

import com.glowxq.core.common.entity.base.BaseEntity;
import com.glowxq.mysql.EntityChangeListener;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.time.LocalDateTime;

/**
 * <p>
 * 部门表
 * </p>
 *
 * @author glowxq
 * @since 2024-03-20
 */
@Data
@Table(value = "sys_dept", onInsert = EntityChangeListener.class, onUpdate = EntityChangeListener.class)
@Schema(description = "部门表")
public class SysDept implements BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    @Schema(description = "id")
    private Long id;

    @Schema(description = "部门名称")
    private String name;

    @Schema(description = "父级id")
    private Long pid;

    @Schema(description = "层级")
    private Integer deep;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "是否有子级")
    private String hasChildren;

    @Schema(description = "是否锁定")
    private String isLock;

    @Schema(description = "负责人")
    @Column("user_id")
    private Long userId;

    @Schema(description = "联系电话")
    @Column("phone")
    private String phone;

    @Schema(description = "邮箱")
    @Column("email")
    private String email;

    @Schema(description = "主管部门")
    @Column("principal")
    private String principal;

    @Schema(description = "主管部门编号")
    @Column("principal_number")
    private String principalNumber;

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

    @Column(isLogicDelete = true)
    @Schema(description = "删除标识")
    private String delFlag;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建人")
    private Long createId;

    @Schema(description = "更新人")
    private Long updateId;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "租户ID")
    private String tenantId;

}