package com.glowxq.system.admin.pojo.vo.sysclient;

import com.glowxq.security.pojo.ClientVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * <p>
 * SysClient查询返回
 * </p>
 *
 * @author glowxq
 * @since 2024-01-22
 */
@Data
@Schema(description = "SysClient返回vo")
public class SysClientVO extends ClientVO {

    @Schema(description = "是否锁定")
    private String isLock;

}