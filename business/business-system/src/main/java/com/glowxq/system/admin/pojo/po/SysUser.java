package com.glowxq.system.admin.pojo.po;

import com.glowxq.core.common.entity.base.BaseEntity;
import com.glowxq.platform.listener.TableSysUserListener;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.time.LocalDateTime;

/**
 * <p>
 * 系统用户表
 * </p>
 *
 * @author glowxq
 * @since 2023-08-24
 */
@Data
@Table(value = "sys_user", onInsert = TableSysUserListener.class, onUpdate = TableSysUserListener.class)
@Schema(description = "系统用户表")
public class SysUser implements BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    @Schema(description = "id")
    private Long id;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "密码")
    private String pwd;

    @Schema(description = "手机号")
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

    @Schema(description = "年龄")
    private Integer age;

    @Schema(description = "身份证")
    private String idCard;

    @Schema(description = "邮箱地址")
    private String email;

    @Schema(description = "地址")
    private String address;

    @Schema(description = "数据权限")
    private String dataScope;

    @Schema(description = "微信openid")
    private String openid;

    @Schema(description = "账户状态 (如 冻结；禁言；正常。 关联字典表account_status)")
    private String accountStatusCd;

    @Schema(description = "标签（自定义关联到字典表）")
    private String userTagCd;

    @Schema(description = "最近一次登录时间")
    private LocalDateTime lastLoginTime;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "租户ID")
    private String tenantId;

    @Schema(description = "是否删除")
    private String delFlag;

    @Schema(description = "创建人")
    private Long createId;

    @Schema(description = "更新人")
    private Long updateId;
}
