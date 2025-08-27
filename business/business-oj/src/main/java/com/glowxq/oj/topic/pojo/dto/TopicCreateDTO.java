package com.glowxq.oj.topic.pojo.dto;

import com.glowxq.core.common.entity.base.BaseDTO;
import com.glowxq.core.util.BeanCopyUtils;
import com.glowxq.oj.topic.pojo.po.Topic;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * Topic添加DTO
 * </p>
 *
 * @author glowxq
 * @since 2025-04-12
 */
@Data
@Schema(description = "Topic添加DTO")
public class TopicCreateDTO implements BaseDTO {

   @Schema(description =  "主题名")
   private String name;

   @Schema(description =  "主题代码")
   private String code;

   @Schema(description =  "负责人ID")
   private Long principalUserId;

   @Schema(description =  "负责人姓名")
   private String principalName;

   @Schema(description =  "主题密码(密码不为空,则打开主题时必须输入正确密码)")
   private String password;

   @Schema(description =  "主题描述")
   private String description;

   @Schema(description =  "时间范围策略")
   private String timeRangeType;

   @Schema(description =  "主题类型")
   private String topicType;

   @Schema(description =  "主题测评类型 ACM/OI")
   private Integer topicJudgeType;

   @Schema(description =  "提前封榜(时) -1为全程封榜 0为不封榜")
   private Integer sealedTime;

   @Schema(description =  "时间限制(分) 超时自动提交")
   private Integer timeLimit;

   @Schema(description =  "罚时(分)")
   private Integer punishmentTime;

   @Schema(description =  "OI得分类型 最近得分/最高得分")
   private String oiScoreType;

   @Schema(description =  "主题颜色")
   private String color;

   @Schema(description =  "公共主题")
   private Boolean common;

   @Schema(description =  "启用")
   private Boolean enable;

   @Schema(description =  "开始时间")
   @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
   private LocalDateTime startTime;

   @Schema(description =  "截止时间")
   @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
   private LocalDateTime endTime;



   @Schema(description = "标签ID")
   private List<Long> tagIds;

   @Schema(description = "题目ID")
   private List<Long> problemIds;


   @Override
    public Topic buildEntity() {
        return BeanCopyUtils.copy(this, Topic.class);
    }
}