package com.glowxq.oj.judge.service.impl;

import com.glowxq.core.common.entity.ImportExcelDTO;
import com.glowxq.core.common.entity.PageResult;
import com.glowxq.core.common.entity.SelectIdsDTO;
import com.glowxq.core.common.enums.CommonResponseEnum;
import com.glowxq.core.util.BeanCopyUtils;
import com.glowxq.core.util.FileUtils;
import com.glowxq.core.util.PageUtils;
import com.glowxq.core.util.Utils;
import com.glowxq.excel.core.ExcelResult;
import com.glowxq.excel.utils.ExcelUtils;
import com.glowxq.oj.judge.mapper.JudgeServerMapper;
import com.glowxq.oj.judge.pojo.dto.JudgeServerCreateDTO;
import com.glowxq.oj.judge.pojo.dto.JudgeServerImportDTO;
import com.glowxq.oj.judge.pojo.dto.JudgeServerListDTO;
import com.glowxq.oj.judge.pojo.dto.JudgeServerUpdateDTO;
import com.glowxq.oj.judge.pojo.po.JudgeServer;
import com.glowxq.oj.judge.pojo.vo.JudgeServerVO;
import com.glowxq.oj.judge.service.JudgeServerService;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 测评服务 服务实现类
 * </p>
 *
 * @author glowxq
 * @since 2025-03-17
 */
@Service
@RequiredArgsConstructor
public class JudgeServerServiceImpl extends ServiceImpl<JudgeServerMapper, JudgeServer> implements JudgeServerService {
    @Override
    public void create(JudgeServerCreateDTO dto){
        JudgeServer judgeServer = BeanCopyUtils.copy(dto, JudgeServer.class);
        save(judgeServer);
    }

    @Override
    public void update(JudgeServerUpdateDTO dto){
        JudgeServer judgeServer = BeanCopyUtils.copy(dto, JudgeServer.class);
        QueryWrapper wrapper;
        // id有效性校验
        wrapper = QueryWrapper.create()
            .eq(JudgeServer::getId, dto.getId());
        CommonResponseEnum.INVALID_ID.assertTrue(count(wrapper) <= 0);

        saveOrUpdate(judgeServer);
    }

    @Override
    public PageResult<JudgeServerVO> page(JudgeServerListDTO dto){
        Page<JudgeServerVO> page = pageAs(PageUtils.getPage(dto), buildQueryWrapper(dto), JudgeServerVO.class);
        return PageUtils.getPageResult(page);
    }

    @Override
    public List<JudgeServerVO> list(JudgeServerListDTO dto){
        return listAs(buildQueryWrapper(dto), JudgeServerVO.class);
    }

    @Override
    public void remove(SelectIdsDTO dto){
        CommonResponseEnum.INVALID_ID.assertTrue(dto.getIds().isEmpty());
        removeByIds(dto.getIds());
    }

    @Override
    public JudgeServerVO detail(Object id){
        JudgeServer judgeServer = getById((Serializable) id);
        CommonResponseEnum.INVALID_ID.assertNull(judgeServer);
        return BeanCopyUtils.copy(judgeServer, JudgeServerVO.class);
    }

    @SneakyThrows
    @Override
    public void importExcel(ImportExcelDTO dto) {
        ExcelResult<JudgeServerImportDTO> excelResult = ExcelUtils.importExcel(dto.getFile().getInputStream(), JudgeServerImportDTO.class, true);
        List<JudgeServerImportDTO> list = excelResult.getList();
        List<String> errorList = excelResult.getErrorList();
        String analysis = excelResult.getAnalysis();
        System.out.println(" analysis : " + analysis);
        System.out.println(" isCover : " + dto.getIsCover());
    }

    @SneakyThrows
    @Override
    public void exportExcel(JudgeServerListDTO dto, HttpServletResponse response) {
        List<JudgeServerVO> list = list(dto);
        String fileName = "测评服务模板";
        OutputStream os = FileUtils.getOutputStream(response, fileName + ".xlsx");
        ExcelUtils.exportExcel(list, "测评服务", JudgeServerVO.class, os);
    }

    private static QueryWrapper buildQueryWrapper(JudgeServerListDTO dto) {
        QueryWrapper wrapper = QueryWrapper.create().from(JudgeServer.class);
        wrapper.like(JudgeServer::getName, dto.getName(), Utils.isNotNull(dto.getName()));
        wrapper.eq(JudgeServer::getIp, dto.getIp(), Utils.isNotNull(dto.getIp()));
        wrapper.eq(JudgeServer::getPort, dto.getPort(), Utils.isNotNull(dto.getPort()));
        wrapper.eq(JudgeServer::getUrl, dto.getUrl(), Utils.isNotNull(dto.getUrl()));
        wrapper.eq(JudgeServer::getCpuCore, dto.getCpuCore(), Utils.isNotNull(dto.getCpuCore()));
        wrapper.eq(JudgeServer::getFreeMemory, dto.getFreeMemory(), Utils.isNotNull(dto.getFreeMemory()));
        wrapper.eq(JudgeServer::getTaskNumber, dto.getTaskNumber(), Utils.isNotNull(dto.getTaskNumber()));
        wrapper.eq(JudgeServer::getMaxTaskNumber, dto.getMaxTaskNumber(), Utils.isNotNull(dto.getMaxTaskNumber()));
        wrapper.eq(JudgeServer::getEnable, dto.getEnable(), Utils.isNotNull(dto.getEnable()));
        wrapper.eq(JudgeServer::getRemoteEnable, dto.getRemoteEnable(), Utils.isNotNull(dto.getRemoteEnable()));
        wrapper.eq(JudgeServer::getCfSubmittableEnable, dto.getCfSubmittableEnable(), Utils.isNotNull(dto.getCfSubmittableEnable()));
        return wrapper;
    }
}