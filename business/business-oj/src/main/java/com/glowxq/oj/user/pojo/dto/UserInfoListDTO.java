package com.glowxq.oj.user.pojo.dto;

import com.glowxq.core.common.entity.PageQuery;
import com.glowxq.core.common.entity.base.BaseDTO;
import com.glowxq.core.util.BeanCopyUtils;
import com.glowxq.oj.user.pojo.po.UserInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * UserInfo查询DTO
 * </p>
 *
 * @author glowxq
 * @since 2025-06-23
 */
@Data
@Schema(description = "UserInfo查询DTO")
public class UserInfoListDTO extends PageQuery implements BaseDTO {

    @Schema(description =  "用户ID")
    private Long userId;

    @Schema(description =  "部门ID")
    private Long deptId;

    @Schema(description =  "姓名")
    private String name;

    @Schema(description =  "昵称")
    private String nickName;

    @Schema(description =  "手机号")
    private String phone;

    @Schema(description =  "邮箱")
    private String email;

    @Schema(description =  "头像信息")
    private String avatar;

    @Schema(description =  "性别")
    private String sex;

    @Schema(description =  "生日开始")
    private LocalDate birthdayStart;

    @Schema(description =  "生日结束")
    private LocalDate birthdayEnd;

    @Schema(description =  "相关图片")
    private String image;

    @Schema(description =  "ac数量")
    private Integer acNum;

    @Schema(description =  "积分")
    private Integer integral;

    @Schema(description =  "连续签到时间")
    private Integer continuousSignDay;

    @Schema(description =  "提交题目数量")
    private Integer submitNum;

    @Schema(description =  "称号")
    private String title;

    @Schema(description =  "颜色")
    private String color;

    @Schema(description =  "帐号过期时间开始")
    private LocalDate expirationTimeStart;

    @Schema(description =  "帐号过期时间结束")
    private LocalDate expirationTimeEnd;

    @Schema(description =  "最后登录时间开始")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastLoginTimeStart;

    @Schema(description =  "最后登录时间结束")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastLoginTimeEnd;

    @Schema(description =  "最后登录ip")
    private String lastLoginIp;

    @Schema(description =  "租户ID")
    private String tenantId;

    @Schema(description =  "搜索关键字")
    private String searchKey;

    /**
     * 标签ids
     */
    private List<Long> tagIds;



    @Override
    public UserInfo buildEntity() {
        return BeanCopyUtils.copy(this, UserInfo.class);
    }
}