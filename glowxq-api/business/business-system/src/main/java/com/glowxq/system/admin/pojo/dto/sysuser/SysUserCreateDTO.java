package com.glowxq.system.admin.pojo.dto.sysuser;

import com.glowxq.core.common.entity.base.BaseDTO;
import com.glowxq.core.common.entity.base.BaseEntity;
import com.glowxq.core.util.BeanCopyUtils;
import com.glowxq.system.admin.pojo.po.SysUser;
import com.glowxq.system.base.user.SysUserDTO;
import com.glowxq.system.enums.DictUserStatusEnum;
import com.glowxq.system.enums.DictUserTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * SysUserCreateDTO
 *
 * @author glowxq
 * @since 2023/8/23
 */
@Data
@Schema(description = "SysUser添加DTO")
public class SysUserCreateDTO implements BaseDTO, SysUserDTO {

    @Schema(description = "账户", requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "真实姓名")
    private String name;

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "小程序id")
    private String openid;

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
    private String accountStatusCd = DictUserStatusEnum.Normal.getCode();

    @Schema(description = "标签")
    private String userTagCd = DictUserTypeEnum.Admin.getCode();

    @Schema(description = "生日")
    private String birthday;

    @Schema(description = "部门ID")
    private Long deptId;

    /**
     * 标签ids
     */
    private List<Long> tagIds;

    @Schema(description = "租户id")
    private String tenantId;

    @Override
    public BaseEntity buildEntity() {
        return BeanCopyUtils.copy(this, SysUser.class);
    }
}
