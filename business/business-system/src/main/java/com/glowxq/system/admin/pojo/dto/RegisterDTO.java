package com.glowxq.system.admin.pojo.dto;

import com.glowxq.core.common.entity.base.BaseDTO;
import com.glowxq.system.admin.pojo.po.SysUser;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author glowxq
 * @version 1.0
 * @date 2025/5/30
 */

@Data
public class RegisterDTO implements BaseDTO {

    @NotBlank
    @Schema(description = "用户名")
    private String username;

    @NotBlank
    @Schema(description = "密码")
    private String password;

    @Schema(description = "租户key，租户id 或 租户code都可以")
    @NotBlank
    private String tenantKey;

    @Schema(description = "注册验证码")
    private String registerCode;

    @Schema(description = "业务代码code")
    private String businessCode;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "电话")
    private String phone;

    @Schema(description = "真实姓名")
    private String name;

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "性别(0 未知 1 男 2 女)")
    private Integer sex;

    @Schema(description = "生日")
    private String birthday;

    @Schema(description = "头像地址")
    private String logo;

    @Schema(description = "地址")
    private String address;

    @Override
    public SysUser buildEntity() {
        SysUser sysUser = new SysUser();
        sysUser.setUsername(this.getUsername());
        sysUser.setPwd(this.getPassword());
        sysUser.setPhone(this.getPhone());
        sysUser.setNickname(this.getNickname());
        sysUser.setAddress(this.getAddress());
        sysUser.setName(this.getName());
        sysUser.setSex(this.getSex());
        sysUser.setBirthday(this.getBirthday());
        sysUser.setLogo(this.getLogo());
        sysUser.setEmail(this.getEmail());
        sysUser.setLastLoginTime(LocalDateTime.now());
        sysUser.setCreateTime(LocalDateTime.now());
        sysUser.setUpdateTime(LocalDateTime.now());
        return sysUser;
    }
}
