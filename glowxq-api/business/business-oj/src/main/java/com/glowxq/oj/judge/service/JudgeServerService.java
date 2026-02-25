package com.glowxq.oj.judge.service;

import com.glowxq.core.common.entity.ImportExcelDTO;
import com.glowxq.core.common.entity.PageResult;
import com.glowxq.core.common.entity.SelectIdsDTO;
import com.glowxq.oj.judge.pojo.dto.JudgeServerCreateDTO;
import com.glowxq.oj.judge.pojo.dto.JudgeServerListDTO;
import com.glowxq.oj.judge.pojo.dto.JudgeServerUpdateDTO;
import com.glowxq.oj.judge.pojo.po.JudgeServer;
import com.glowxq.oj.judge.pojo.vo.JudgeServerVO;
import com.mybatisflex.core.service.IService;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

/**
 * <p>
 * 测评服务 Service
 * </p>
 *
 * @author glowxq
 * @since 2025-03-17
 */
public interface JudgeServerService extends IService<JudgeServer> {

    void create(JudgeServerCreateDTO dto);

    void update(JudgeServerUpdateDTO dto);

    PageResult<JudgeServerVO> page(JudgeServerListDTO dto);

    List<JudgeServerVO> list(JudgeServerListDTO dto);

    void remove(SelectIdsDTO dto);

    JudgeServerVO detail(Object id);

    void importExcel(ImportExcelDTO dto);

    void exportExcel(JudgeServerListDTO dto, HttpServletResponse response);
}