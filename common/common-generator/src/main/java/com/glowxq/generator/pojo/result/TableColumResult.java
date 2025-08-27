package com.glowxq.generator.pojo.result;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * @author glowxq
 * @since 2023/11/27 15:30
 */
@Data
public class TableColumResult {

    /**
     * 字段名
     */
    private String columnName;

    /**
     * 是否必填
     */
    private String isRequired;

    /**
     * 是否主键
     */
    private String isPk;

    /**
     * 排序
     */
    private int sort;

    /**
     * comment描述
     */
    private String columnComment;

    /**
     * 是否自增
     */
    private String isIncrement;

    /**
     * 字段类型
     */
    private String columnType;

    public String getRemoveUnsignedType(){
        return StringUtils.remove(columnType,"unsigned").trim();
    }

}
