package com.glowxq.oj.judge.pojo.po;

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
* 测评服务
* </p>
*
* @author glowxq
* @since 2025-03-17
*/
@Data
@Table(value = "judge_server", onInsert = EntityChangeListener.class, onUpdate = EntityChangeListener.class)
@Schema(description = "测评服务")
public class JudgeServer implements BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    @Schema(description ="ID")
    private Long id;

    @Schema(description ="判题服务名字")
    private String name;

    @Schema(description ="判题机ip")
    private String ip;

    @Schema(description ="判题机端口号")
    private Integer port;

    @Schema(description ="ip:port")
    private String url;

    @Schema(description ="判题机所在服务器cpu核心数")
    private Integer cpuCore;

    @Schema(description ="判题机所在服务器空闲内存")
    private Integer freeMemory;

    @Schema(description ="当前判题数")
    private Integer taskNumber;

    @Schema(description ="判题并发最大数")
    private Integer maxTaskNumber;

    @Schema(description ="0禁用|1启用")
    private Boolean enable;

    @Schema(description ="是否开启远程判题vj")
    private Boolean remoteEnable;

    @Schema(description ="是否可提交CF")
    private Boolean cfSubmittableEnable;

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