package com.glowxq.generator.service;

import com.mybatisflex.core.service.IService;
import com.glowxq.core.common.entity.PageResult;
import com.glowxq.generator.pojo.dto.DbTableQueryDTO;
import com.glowxq.generator.pojo.dto.ImportTableDTO;
import com.glowxq.generator.pojo.dto.SelectTablesDTO;
import com.glowxq.generator.pojo.po.GeneratorTable;
import com.glowxq.generator.pojo.vo.GenCheckedInfoVO;
import com.glowxq.generator.pojo.vo.GeneratorDetailVO;
import com.glowxq.generator.pojo.vo.GeneratorPreviewVO;
import freemarker.template.Template;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

/**
 * <p>
 * 代码生成业务表 服务类
 * </p>
 *
 * @author glowxq
 * @since 2023-11-27
 */
public interface GeneratorTableService extends IService<GeneratorTable> {

    void importTable(ImportTableDTO dto);

    PageResult<GeneratorTable> selectDbTableNotInImport(DbTableQueryDTO dto);

    PageResult<GeneratorTable> selectDbTableByImport(DbTableQueryDTO dto);

    GeneratorDetailVO detail(String tableName);

    void updateGeneratorSetting(GeneratorDetailVO generatorDetailVO);

    List<String> generator(String tableName) throws IOException;

    GenCheckedInfoVO checkDist(String tableName);

    byte[] downloadZip(SelectTablesDTO dto) throws IOException;

    List<GeneratorPreviewVO> preview(String tableName) throws IOException;

    Template getMenuSqlTemplate() throws IOException;

    @Transactional
    void remove(SelectTablesDTO dto);

    Template getDictSqlTemplate() throws IOException;
}
