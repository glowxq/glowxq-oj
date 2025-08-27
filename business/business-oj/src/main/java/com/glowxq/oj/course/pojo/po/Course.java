package com.glowxq.oj.course.pojo.po;

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
* 课程
* </p>
*
* @author glowxq
* @since 2025-03-30
*/
@Data
@Table(value = "course", onInsert = EntityChangeListener.class, onUpdate = EntityChangeListener.class)
@Schema(description = "课程")
public class Course implements BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    @Schema(description ="")
    private Long id;

    @Schema(description ="课程名")
    private String name;

    @Schema(description ="课程内容")
    private String content;

    @Schema(description ="课程连接")
    private String url;

    @Schema(description ="老师用户id")
    private Long teacherUserId;

    @Schema(description ="老师姓名")
    private String teacherName;

    @Schema(description ="老师电话")
    private String teacherPhone;

    @Schema(description ="课程状态")
    private String status;

    @Schema(description ="启用状态")
    private Boolean enable;

    @Schema(description ="上课时间")
    private LocalDateTime startTime;

    @Schema(description ="下课时间")
    private LocalDateTime endTime;

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