package com.glowxq.generator.service.impl;

import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.glowxq.generator.mapper.GeneratorTableColumnMapper;
import com.glowxq.generator.pojo.po.GeneratorTableColumn;
import com.glowxq.generator.pojo.po.table.GeneratorTableColumnTableDef;
import com.glowxq.generator.pojo.po.table.GeneratorTableTableDef;
import com.glowxq.generator.service.GeneratorTableColumnService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 代码生成业务表字段 服务实现类
 * </p>
 *
 * @author glowxq
 * @since 2023-11-27
 */
@Service
public class GeneratorTableColumnServiceImpl extends ServiceImpl<GeneratorTableColumnMapper, GeneratorTableColumn> implements GeneratorTableColumnService {

    @Override
    public void batchInsert(List<GeneratorTableColumn> tableColumns) {
        saveBatch(tableColumns);
    }

    @Override
    public List<GeneratorTableColumn> getTableColumnsByTableId(Long tableId) {
        QueryWrapper wrapper = QueryWrapper.create().eq(GeneratorTableColumn::getTableId, tableId).orderBy(GeneratorTableColumn::getSort).asc();
        return list(wrapper);
    }

    @Override
    public List<GeneratorTableColumn> getTableColumnsByTableName(Long tableId) {
        return QueryChain.of(mapper).eq(GeneratorTableColumn::getTableId, tableId).orderBy(GeneratorTableColumn::getSort).asc().list();
    }

    @Override
    public void updateBatchTableColumns(List<GeneratorTableColumn> columns) {
        updateBatch(columns);
    }

    @Override
    public void remove(List<String> tableNames) {
        this.updateChain().from(GeneratorTableTableDef.GENERATOR_TABLE, GeneratorTableColumnTableDef.GENERATOR_TABLE_COLUMN)
                .where(GeneratorTableColumnTableDef.GENERATOR_TABLE_COLUMN.TABLE_ID.eq(GeneratorTableTableDef.GENERATOR_TABLE.TABLE_ID))
                .where(GeneratorTableTableDef.GENERATOR_TABLE.TABLE_NAME.in(tableNames)).remove();
    }

}
