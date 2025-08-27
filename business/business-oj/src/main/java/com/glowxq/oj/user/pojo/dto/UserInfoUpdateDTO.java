package com.glowxq.oj.user.pojo.dto;

import com.glowxq.core.common.entity.base.BaseDTO;
import com.glowxq.core.util.BeanCopyUtils;
import com.glowxq.core.util.DateUtils;
import com.glowxq.oj.user.pojo.po.UserInfo;
import com.glowxq.system.admin.pojo.dto.sysuser.SysUserUpdateDTO;
import com.glowxq.system.base.user.UserDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * UserInfo修改DTO
 * </p>
 *
 * @author glowxq
 * @since 2025-06-23
 */
@Data
@Schema(description = "UserInfo修改DTO")
public class UserInfoUpdateDTO implements BaseDTO, UserDTO {

    @Schema(description = "ID")
    private Long id;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "部门ID")
    private Long deptId;

    @Schema(description = "姓名")
    private String name;

    @Schema(description = "昵称")
    private String nickName;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "头像信息")
    private String avatar;

    @Schema(description = "性别")
    private String sex;

    @Schema(description = "生日")
    private LocalDate birthday;

    @Schema(description = "相关图片")
    private String image;

    @Schema(description = "ac数量")
    private Integer acNum;

    @Schema(description = "积分")
    private Integer integral;

    @Schema(description = "连续签到时间")
    private Integer continuousSignDay;

    @Schema(description = "提交题目数量")
    private Integer submitNum;

    @Schema(description = "称号")
    private String title;

    @Schema(description = "颜色")
    private String color;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "帐号过期时间")
    private LocalDate expirationTime;

    @Schema(description = "最后登录时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastLoginTime;

    @Schema(description = "最后登录ip")
    private String lastLoginIp;

    @Schema(description = "租户ID")
    private String tenantId;

    /**
     * 标签ids
     */
    private List<Long> tagIds;



    @Schema(description = "角色id数组")
    private List<Long> roleIds;

    @Override
    public UserInfo buildEntity() {
        return BeanCopyUtils.copy(this, UserInfo.class);
    }

    @Override
    public SysUserUpdateDTO buildSysUserDTO() {
        SysUserUpdateDTO sysUserCreateDto = new SysUserUpdateDTO();
        sysUserCreateDto.setId(id);
        sysUserCreateDto.setUsername(phone);
        sysUserCreateDto.setPhone(phone);
        sysUserCreateDto.setName(name);
        sysUserCreateDto.setNickname(nickName);
        sysUserCreateDto.setLogo(avatar);
        sysUserCreateDto.setAge(0);
        sysUserCreateDto.setSex(0);
        sysUserCreateDto.setIdCard("");
        sysUserCreateDto.setDataScope("");
        sysUserCreateDto.setEmail(email);
        sysUserCreateDto.setBirthday(DateUtils.formatDateTime(birthday));
        sysUserCreateDto.setTagIds(tagIds);
        return sysUserCreateDto;
    }
}