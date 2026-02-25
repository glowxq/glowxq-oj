package com.glowxq.system.admin.pojo.dto.sysuser;

import com.glowxq.core.common.entity.PageQuery;
import com.glowxq.core.common.valid.annotation.InEnum;
import com.glowxq.system.admin.enums.UserQueryType;
import com.glowxq.system.base.user.SysUserDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * SysUserQueryDTO
 *
 * @author glowxq
 * @since 2023/8/23
 */
@Data
public class SysUserListDTO extends PageQuery implements SysUserDTO {

    @Schema(description = "账户")
    private String username;

    @Schema(description = "真实姓名")
    private String name;

    @Schema(description = "姓名")
    private String nickname;

    @Schema(description = "部门列表。0查询全部，-1 查询未分配部门的用户")
    private Long deptId;

    @Schema(description = "是否只查询当前层级")
    private Boolean isThisDeep;

    @Schema(description = "电话")
    private String phone;

    @Schema(description = "租户ID")
    private String tenantId;

    @InEnum(enumClass = UserQueryType.class)
    @Schema(description = "用户查询类型")
    @NotNull
    private String userQueryType = UserQueryType.Dept.getCode();

    @Schema(description = "班级ID")
    private List<Long> groupIds;

    @Schema(description = "标签ID")
    private List<Long> tagIds;

    public UserQueryType userQueryType() {
        return UserQueryType.matchCode(userQueryType);
    }
}
