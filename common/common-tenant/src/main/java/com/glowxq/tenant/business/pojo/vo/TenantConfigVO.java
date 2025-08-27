package com.glowxq.tenant.business.pojo.vo;

import com.glowxq.core.common.entity.base.BaseVO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author glowxq
 * @version 1.0
 * @date 2025/6/10
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TenantConfigVO implements BaseVO {

    /**
     * 是否启用多租户功能
     * <p>
     * 默认为true，设置为false时将完全禁用多租户功能
     * </p>
     */
    private Boolean enable;

    /**
     * 需要忽略租户过滤的表名列表
     * <p>
     * 这些表在查询时将不添加租户条件，通常用于系统表、配置表等全局共享的数据
     * </p>
     */
    private List<String> ignoreTable;
}
