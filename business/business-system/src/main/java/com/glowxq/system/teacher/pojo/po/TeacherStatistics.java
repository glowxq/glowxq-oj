package com.glowxq.system.teacher.pojo.po;

import com.glowxq.core.common.entity.base.BaseEntity;
import com.glowxq.mysql.EntityChangeListener;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import com.mybatisflex.core.handler.JacksonTypeHandler;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 教师统计总览表
 * </p>
 *
 * @author glowxq
 * @since 2024-02-19
 */
@Data
@Table(value = "teacher_statistics", onInsert = EntityChangeListener.class, onUpdate = EntityChangeListener.class)
@Schema(description = "教师统计总览表")
public class TeacherStatistics implements BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    @Schema(description = "id")
    private Long id;

    @Schema(description = "统计年限")
    private String year;

    @Schema(description = "统计月份")
    private String month;

    @Schema(description = "统计年月")
    private String duringTime;

    @Schema(description = "教师id")
    private String teacherId;

    @Schema(description = "讲师区分类型")
    private Integer teacherCommonType;

    @Schema(description = "授课总数")
    private Integer totalTeaching;

    @Schema(description = "服务班次数")
    private Integer totalClassCount;

    @Schema(description = "课时总数")
    private BigDecimal totalHours;

    @Schema(description = "核对状态")
    private Integer checkStatus;

    @Schema(description = "核对时间")
    private LocalDateTime checkTime;

    @Schema(description = "生成时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "租户ID")
    private String tenantId;

    @Schema(description = "最近一次同步时间")
    private LocalDateTime lastSyncTime;

    @Schema(description = "备注")
    private String remark;

    private Long createId;

    private Long updateId;

    @Schema(description = "部门范围id")
    @Column(typeHandler = JacksonTypeHandler.class)
    private List<Long> deptScope;

}