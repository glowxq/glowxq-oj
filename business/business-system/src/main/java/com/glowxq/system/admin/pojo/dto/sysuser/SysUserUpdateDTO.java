package com.glowxq.system.admin.pojo.dto.sysuser;

import com.glowxq.core.common.entity.base.BaseDTO;
import com.glowxq.core.common.entity.base.BaseEntity;
import com.glowxq.core.util.BeanCopyUtils;
import com.glowxq.system.admin.pojo.po.SysUser;
import com.glowxq.system.base.user.SysUserDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * SysUserUpdateDTO
 *
 * @author glowxq
 * @since 2023/8/23
 */
@Data
@Schema(description = "SysUser修改DTO")
public class SysUserUpdateDTO implements BaseDTO, SysUserDTO {

    @Schema(description = "用户id")
    private Long id;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "账号")
    private String username;

    @Schema(description = "真实姓名")
    private String name;

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "头像")
    private String logo;

    @Schema(description = "年龄")
    private Integer age;

    @Schema(description = "性别")
    private Integer sex;

    @Schema(description = "身份证")
    private String idCard;

    @Schema(description = "数据权限")
    private String dataScope;

    @Schema(description = "邮箱地址")
    private String email;

    @Schema(description = "状态")
    private String accountStatusCd;

    @Schema(description = "标签")
    private String userTagCd;

    @Schema(description = "生日")
    private String birthday;

    /**
     * 标签ids
     */
    private List<Long> tagIds;

    @Override
    public BaseEntity buildEntity() {
        return BeanCopyUtils.copy(this, SysUser.class);
    }
}
