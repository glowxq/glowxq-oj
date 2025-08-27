package com.glowxq.oj.user.pojo.dto;

import cn.idev.excel.annotation.ExcelProperty;
import com.glowxq.core.common.entity.base.BaseDTO;
import com.glowxq.core.util.BeanCopyUtils;
import com.glowxq.oj.user.pojo.po.UserInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
/**
 * <p>
 * UserInfo导入DTO
 * </p>
 *
 * @author glowxq
 * @since 2025-06-23
 */
@Data
@Schema(description = "UserInfo导入DTO")
public class UserInfoImportDTO implements BaseDTO {

    @ExcelProperty(value = "用户ID")
    @Schema(description =  "用户ID")
    private Long userId;

    @ExcelProperty(value = "部门ID")
    @Schema(description =  "部门ID")
    private Long deptId;

    @ExcelProperty(value = "姓名")
    @Schema(description =  "姓名")
    private String name;

    @ExcelProperty(value = "昵称")
    @Schema(description =  "昵称")
    private String nickName;

    @ExcelProperty(value = "手机号")
    @Schema(description =  "手机号")
    private String phone;

    @ExcelProperty(value = "邮箱")
    @Schema(description =  "邮箱")
    private String email;

    @ExcelProperty(value = "头像信息")
    @Schema(description =  "头像信息")
    private String avatar;

    @ExcelProperty(value = "性别")
    @Schema(description =  "性别")
    private String sex;

    @ExcelProperty(value = "生日")
    @Schema(description =  "生日")
    private LocalDate birthday;

    @ExcelProperty(value = "相关图片")
    @Schema(description =  "相关图片")
    private String image;

    @ExcelProperty(value = "ac数量")
    @Schema(description =  "ac数量")
    private Integer acNum;

    @ExcelProperty(value = "积分")
    @Schema(description =  "积分")
    private Integer integral;

    @ExcelProperty(value = "连续签到时间")
    @Schema(description =  "连续签到时间")
    private Integer continuousSignDay;

    @ExcelProperty(value = "提交题目数量")
    @Schema(description =  "提交题目数量")
    private Integer submitNum;

    @ExcelProperty(value = "称号")
    @Schema(description =  "称号")
    private String title;

    @ExcelProperty(value = "颜色")
    @Schema(description =  "颜色")
    private String color;

    @ExcelProperty(value = "备注")
    @Schema(description =  "备注")
    private String remark;

    @ExcelProperty(value = "帐号过期时间")
    @Schema(description =  "帐号过期时间")
    private LocalDate expirationTime;

    @Schema(description =  "最后登录时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastLoginTime;

    @ExcelProperty(value = "最后登录ip")
    @Schema(description =  "最后登录ip")
    private String lastLoginIp;

    @ExcelProperty(value = "租户ID")
    @Schema(description =  "租户ID")
    private String tenantId;

    @Override
    public UserInfo buildEntity() {
        return BeanCopyUtils.copy(this, UserInfo.class);
    }
}