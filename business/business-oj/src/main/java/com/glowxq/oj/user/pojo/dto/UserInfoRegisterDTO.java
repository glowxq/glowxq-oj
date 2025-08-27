package com.glowxq.oj.user.pojo.dto;

import com.glowxq.core.common.entity.base.BaseDTO;
import com.glowxq.core.util.BeanCopyUtils;
import com.glowxq.core.util.DateUtils;
import com.glowxq.oj.user.pojo.po.UserInfo;
import com.glowxq.system.admin.pojo.dto.sysuser.SysUserCreateDTO;
import com.glowxq.system.base.user.UserDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

/**
 * <p>
 * UserInfo添加DTO
 * </p>
 *
 * @author glowxq
 * @since 2025-06-23
 */
@Data
@Schema(description = "UserInfo添加DTO")
public class UserInfoRegisterDTO implements BaseDTO, UserDTO {

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

    @Schema(description = "班级代码")
    private String groupCode;

    @Override
    public UserInfo buildEntity() {
        return BeanCopyUtils.copy(this, UserInfo.class);
    }

    @Override
    public SysUserCreateDTO buildSysUserDTO() {
        SysUserCreateDTO sysUserCreateDto = new SysUserCreateDTO();
        sysUserCreateDto.setUsername(phone);
        sysUserCreateDto.setPhone(phone);
        sysUserCreateDto.setName(name);
        sysUserCreateDto.setNickname(nickName);
        sysUserCreateDto.setOpenid("");
        sysUserCreateDto.setLogo(avatar);
        sysUserCreateDto.setAge(0);
        sysUserCreateDto.setSex(0);
        sysUserCreateDto.setIdCard("");
        sysUserCreateDto.setDataScope("");
        sysUserCreateDto.setEmail(email);
        sysUserCreateDto.setBirthday(DateUtils.formatDateTime(birthday));
        return sysUserCreateDto;
    }
}