package com.glowxq.system.admin.pojo.vo.sysuser;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.glowxq.core.common.entity.base.BaseVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author glowxq
 * @since 2023/8/24 10:29
 */
@Data
public class SysUserVO implements BaseVO {

    @Schema(description = "id")
    private Long id;

    @Schema(description = "username")
    private String username;

    @JsonIgnore // 忽略该字段的序列化和反序列化
    @Schema(description = "密码")
    private String pwd;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "真实姓名")
    private String name;

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "LOGO")
    private String logo;

    @Schema(description = "年龄")
    private Integer age;

    @Schema(description = "性别")
    private Integer sex;

    @Schema(description = "身份证")
    private String idCard;

    @Schema(description = "email")
    private String email;

    @Schema(description = "数据权限")
    private String dataScope;

    @Schema(description = "账号状态码")
    private String accountStatusCd;

    @Schema(description = "userTagCd")
    private String userTagCd;

    @Schema(description = "最近一次登录时间")
    private LocalDateTime lastLoginTime;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "租户ID")
    private String tenantId;

    private String delFlag;

    @Schema(description = "部门信息")
    private String deptInfo;

    @Schema(description = "部门ID，多个逗号分隔")
    private String deptIds;

    @Schema(description = "角色信息")
    private String roleInfo;

    @Schema(description = "角色ID,多个逗号分隔")
    private String roleIds;

    @Schema(description = "用户ID")
    private Long userId;
}
