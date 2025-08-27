package com.glowxq.oj.topic.service.impl;

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
import com.glowxq.oj.topic.mapper.TopicProblemMapper;
import com.glowxq.oj.topic.pojo.dto.TopicProblemCreateDTO;
import com.glowxq.oj.topic.pojo.dto.TopicProblemImportDTO;
import com.glowxq.oj.topic.pojo.dto.TopicProblemListDTO;
import com.glowxq.oj.topic.pojo.dto.TopicProblemUpdateDTO;
import com.glowxq.oj.topic.pojo.po.TopicProblem;
import com.glowxq.oj.topic.pojo.vo.TopicProblemVO;
import com.glowxq.oj.topic.service.TopicProblemService;
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
 * 主题题目 服务实现类
 * </p>
 *
 * @author glowxq
 * @since 2025-04-12
 */
@Service
@RequiredArgsConstructor
public class TopicProblemServiceImpl extends ServiceImpl<TopicProblemMapper, TopicProblem> implements TopicProblemService {

    private static QueryWrapper buildQueryWrapper(TopicProblemListDTO dto) {
        QueryWrapper wrapper = QueryWrapper.create().from(TopicProblem.class);
        wrapper.eq(TopicProblem::getTopicId, dto.getTopicId(), Utils.isNotNull(dto.getTopicId()));
        wrapper.eq(TopicProblem::getProblemId, dto.getProblemId(), Utils.isNotNull(dto.getProblemId()));
        wrapper.eq(TopicProblem::getRequired, dto.getRequired(), Utils.isNotNull(dto.getRequired()));
        return wrapper;
    }

    @Override
    public void create(TopicProblemCreateDTO dto) {
        TopicProblem topicProblem = BeanCopyUtils.copy(dto, TopicProblem.class);
        save(topicProblem);
    }

    @Override
    public void update(TopicProblemUpdateDTO dto) {
        TopicProblem topicProblem = BeanCopyUtils.copy(dto, TopicProblem.class);
        QueryWrapper wrapper;
        // id有效性校验
        wrapper = QueryWrapper.create()
                              .eq(TopicProblem::getId, dto.getId());
        CommonResponseEnum.INVALID_ID.assertTrue(count(wrapper) <= 0);

        saveOrUpdate(topicProblem);
    }

    @Override
    public PageResult<TopicProblemVO> page(TopicProblemListDTO dto) {
        Page<TopicProblemVO> page = pageAs(PageUtils.getPage(dto), buildQueryWrapper(dto), TopicProblemVO.class);
        return PageUtils.getPageResult(page);
    }

    @Override
    public List<TopicProblemVO> list(TopicProblemListDTO dto) {
        return listAs(buildQueryWrapper(dto), TopicProblemVO.class);
    }

    @Override
    public void remove(SelectIdsDTO dto) {
        CommonResponseEnum.INVALID_ID.assertTrue(dto.getIds().isEmpty());
        removeByIds(dto.getIds());
    }

    @Override
    public TopicProblemVO detail(Object id) {
        TopicProblem topicProblem = getById((Serializable) id);
        CommonResponseEnum.INVALID_ID.assertNull(topicProblem);
        return BeanCopyUtils.copy(topicProblem, TopicProblemVO.class);
    }

    @SneakyThrows
    @Override
    public void importExcel(ImportExcelDTO dto) {
        ExcelResult<TopicProblemImportDTO> excelResult = ExcelUtils.importExcel(dto.getFile().getInputStream(), TopicProblemImportDTO.class, true);
        List<TopicProblemImportDTO> list = excelResult.getList();
        List<String> errorList = excelResult.getErrorList();
        String analysis = excelResult.getAnalysis();
        System.out.println(" analysis : " + analysis);
        System.out.println(" isCover : " + dto.getIsCover());
    }

    @SneakyThrows
    @Override
    public void exportExcel(TopicProblemListDTO dto, HttpServletResponse response) {
        List<TopicProblemVO> list = list(dto);
        String fileName = "主题题目模板";
        OutputStream os = FileUtils.getOutputStream(response, fileName + ".xlsx");
        ExcelUtils.exportExcel(list, "主题题目", TopicProblemVO.class, os);
    }

    @Override
    public void bindTopicProblem(Long topicId, List<Long> problemIds) {
        mapper.bindTopicProblem(topicId, problemIds);
    }

    @Override
    public void unBindAllByTopicId(Long topicId) {
        mapper.unBindAllByTopicId(topicId);
    }

    @Override
    public List<TopicProblem> listByTopicId(Long topicId) {
        return mapper.listByTopicId(topicId);
    }
}