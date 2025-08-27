package com.glowxq.generator.pojo.result;

import com.glowxq.core.util.StringUtils;
import lombok.Data;

/**
 * @author glowxq
 * @since 2023/11/27 13:55
 */
@Data
public class TableResult {

    private String tableName;

    private String tableComment;

    private String createTime;

    private String updateTime;

    public String buildModuleName() {
        return StringUtils.getFirstSegment(this.tableName, "_");
    }
}
