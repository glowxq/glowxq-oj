package com.glowxq.oj.topic.pojo.dto;

import com.glowxq.core.common.entity.base.BaseDTO;
import com.glowxq.core.util.BeanCopyUtils;
import com.glowxq.oj.topic.pojo.po.TopicInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * <p>
 * TopicInfo修改DTO
 * </p>
 *
 * @author glowxq
 * @since 2025-04-12
 */
@Data
@Schema(description = "TopicInfo修改DTO")
public class TopicInfoUpdateDTO implements BaseDTO {

    @Schema(description =  "")
    private Long id;

    @Schema(description =  "主题ID")
    private Long topicId;

    @Schema(description =  "用户ID")
    private Long userId;

    @Schema(description =  "头像")
    private String avatar;

    @Schema(description =  "姓名")
    private String name;

    @Schema(description =  "昵称")
    private String nickName;

    @Schema(description =  "总得分")
    private Integer score;

    @Schema(description =  "总AC数量")
    private Integer acNum;

    @Schema(description =  "提交次数")
    private Integer submitNum;

    @Schema(description =  "总做题用时(分)")
    private Integer useTime;

    @Schema(description =  "总罚时(分)")
    private Integer punishmentTime;

    @Schema(description =  "主题状态")
    private String status;

    @Schema(description =  "开始时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    @Schema(description =  "完成时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    @Schema(description =  "自动提交")
    private Boolean autoSubmit;

    @Override
    public TopicInfo buildEntity() {
        return BeanCopyUtils.copy(this, TopicInfo.class);
    }
}