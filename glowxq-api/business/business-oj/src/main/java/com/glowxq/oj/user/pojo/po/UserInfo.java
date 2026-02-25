package com.glowxq.oj.user.pojo.po;

import com.glowxq.core.common.entity.base.BaseEntity;
import com.glowxq.mysql.EntityChangeListener;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * <p>
 * 用户信息
 * </p>
 *
 * @author glowxq
 * @since 2025-06-23
 */
@Data
@Table(value = "user_info", onInsert = EntityChangeListener.class, onUpdate = EntityChangeListener.class)
@Schema(description = "用户信息")
public class UserInfo implements BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.None)
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
    private LocalDateTime lastLoginTime;

    @Schema(description = "最后登录ip")
    private String lastLoginIp;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "是否删除")
    private String delFlag;

    @Schema(description = "创建人ID")
    private Long createId;

    @Schema(description = "更新人ID")
    private Long updateId;

    @Schema(description = "租户ID")
    private String tenantId;

    public void addIntegral(int signAddIntegral) {
        this.integral += signAddIntegral;
    }

    public void plusSubmitNum() {
        this.submitNum += 1;
    }

    public void acNumPlus() {
        this.acNum += 1;
    }
}