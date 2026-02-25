package com.glowxq.oj.code.service;

import com.glowxq.core.common.entity.ImportExcelDTO;
import com.glowxq.core.common.entity.PageResult;
import com.glowxq.core.common.entity.SelectIdsDTO;
import com.glowxq.oj.code.pojo.dto.CodeRecordCreateDTO;
import com.glowxq.oj.code.pojo.dto.CodeRecordListDTO;
import com.glowxq.oj.code.pojo.dto.CodeRecordUpdateDTO;
import com.glowxq.oj.code.pojo.po.CodeRecord;
import com.glowxq.oj.code.pojo.vo.CodeRecordVO;
import com.mybatisflex.core.service.IService;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

/**
 * <p>
 * 用户代码 Service
 * </p>
 *
 * @author glowxq
 * @since 2025-04-04
 */
public interface CodeRecordService extends IService<CodeRecord> {

    void create(CodeRecordCreateDTO dto);

    void update(CodeRecordUpdateDTO dto);

    PageResult<CodeRecordVO> page(CodeRecordListDTO dto);

    List<CodeRecordVO> list(CodeRecordListDTO dto);

    void remove(SelectIdsDTO dto);

    CodeRecordVO detail(Object id);

    void importExcel(ImportExcelDTO dto);

    void exportExcel(CodeRecordListDTO dto, HttpServletResponse response);

    List<CodeRecord> listByUserId(Long userId);
}