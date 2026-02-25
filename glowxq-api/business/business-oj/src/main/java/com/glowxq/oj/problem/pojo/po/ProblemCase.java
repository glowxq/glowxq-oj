package com.glowxq.oj.problem.pojo.po;

import com.glowxq.core.common.entity.base.BaseEntity;
import com.glowxq.mysql.EntityChangeListener;
import com.glowxq.oj.problem.enums.ProblemCaseType;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.time.LocalDateTime;

/**
 * <p>
 * 测试用例
 * </p>
 *
 * @author glowxq
 * @since 2025-03-17
 */
@Data
@Table(value = "problem_case", onInsert = EntityChangeListener.class, onUpdate = EntityChangeListener.class)
@Schema(description = "测试用例")
public class ProblemCase implements BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    @Schema(description = "ID")
    private Long id;

    @Schema(description = "题目ID")
    private Long problemId;

    @Schema(description = "题目KEY")
    private String problemKey;

    @Schema(description = "测试样例的输入")
    private String input;

    @Schema(description = "测试样例的输出")
    private String output;

    @Schema(description = "在线测试样例的输入")
    private String inputUrl;

    @Schema(description = "在线测试样例的输出")
    private String outputUrl;

    @Schema(description = "用例类型")
    private String type;

    @Schema(description = "该测试样例的IO得分")
    private Integer score;

    @Schema(description = "0禁用|1启用")
    private Boolean enable;

    @Schema(description = "subtask分组的编号")
    private Integer groupNum;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "租户ID")
    private String tenantId;

    @Schema(description = "创建人ID")
    private Long createId;

    @Schema(description = "更新人ID")
    private Long updateId;

    public ProblemCaseType problemCaseType() {
        return ProblemCaseType.matchCode(type);
    }
}