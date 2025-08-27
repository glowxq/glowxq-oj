package com.glowxq.generator.mapper;

import com.mybatisflex.core.BaseMapper;
import com.glowxq.generator.pojo.po.GeneratorTableColumn;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 代码生成业务表字段 Mapper 接口
 * </p>
 *
 * @author glowxq
 * @since 2023-11-27
 */
public interface GeneratorTableColumnMapper extends BaseMapper<GeneratorTableColumn> {

    List<GeneratorTableColumn> queryAllByTableName(@Param("tableNames") List<String> tableNames);

}
