package com.glowxq.oj.user.service.impl;

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
import com.glowxq.oj.judge.enums.JudgeStatus;
import com.glowxq.oj.judge.pojo.po.Judge;
import com.glowxq.oj.user.mapper.UserProblemMapper;
import com.glowxq.oj.user.pojo.dto.UserProblemCreateDTO;
import com.glowxq.oj.user.pojo.dto.UserProblemImportDTO;
import com.glowxq.oj.user.pojo.dto.UserProblemListDTO;
import com.glowxq.oj.user.pojo.dto.UserProblemUpdateDTO;
import com.glowxq.oj.user.pojo.po.UserProblem;
import com.glowxq.oj.user.pojo.vo.UserProblemVO;
import com.glowxq.oj.user.service.UserProblemService;
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
 * 用户题目数据 服务实现类
 * </p>
 *
 * @author glowxq
 * @since 2025-04-03
 */
@Service
@RequiredArgsConstructor
public class UserProblemServiceImpl extends ServiceImpl<UserProblemMapper, UserProblem> implements UserProblemService {

    private static QueryWrapper buildQueryWrapper(UserProblemListDTO dto) {
        QueryWrapper wrapper = QueryWrapper.create().from(UserProblem.class);
        wrapper.eq(UserProblem::getUserId, dto.getUserId(), Utils.isNotNull(dto.getUserId()));
        wrapper.eq(UserProblem::getProblemId, dto.getProblemId(), Utils.isNotNull(dto.getProblemId()));
        wrapper.eq(UserProblem::getProblemKey, dto.getProblemKey(), Utils.isNotNull(dto.getProblemKey()));
        wrapper.eq(UserProblem::getProblemTitle, dto.getProblemTitle(), Utils.isNotNull(dto.getProblemTitle()));
        wrapper.eq(UserProblem::getJudgeId, dto.getJudgeId(), Utils.isNotNull(dto.getJudgeId()));
        wrapper.eq(UserProblem::getJudgeStatus, dto.getJudgeStatus(), Utils.isNotNull(dto.getJudgeStatus()));
        wrapper.eq(UserProblem::getScore, dto.getScore(), Utils.isNotNull(dto.getScore()));
        wrapper.eq(UserProblem::getCode, dto.getCode(), Utils.isNotNull(dto.getCode()));
        wrapper.eq(UserProblem::getOptions, dto.getOptions(), Utils.isNotNull(dto.getOptions()));
        wrapper.eq(UserProblem::getFlowImage, dto.getFlowImage(), Utils.isNotNull(dto.getFlowImage()));
        wrapper.eq(UserProblem::getProblemType, dto.getProblemType(), Utils.isNotNull(dto.getProblemType()));
        return wrapper;
    }

    @Override
    public void create(UserProblemCreateDTO dto) {
        UserProblem userProblem = BeanCopyUtils.copy(dto, UserProblem.class);
        save(userProblem);
    }

    @Override
    public void update(UserProblemUpdateDTO dto) {
        UserProblem userProblem = BeanCopyUtils.copy(dto, UserProblem.class);
        QueryWrapper wrapper;
        // id有效性校验
        wrapper = QueryWrapper.create()
                              .eq(UserProblem::getId, dto.getId());
        CommonResponseEnum.INVALID_ID.assertTrue(count(wrapper) <= 0);

        saveOrUpdate(userProblem);
    }

    @Override
    public PageResult<UserProblemVO> page(UserProblemListDTO dto) {
        Page<UserProblemVO> page = pageAs(PageUtils.getPage(dto), buildQueryWrapper(dto), UserProblemVO.class);
        return PageUtils.getPageResult(page);
    }

    @Override
    public List<UserProblemVO> list(UserProblemListDTO dto) {
        return listAs(buildQueryWrapper(dto), UserProblemVO.class);
    }

    @Override
    public void remove(SelectIdsDTO dto) {
        CommonResponseEnum.INVALID_ID.assertTrue(dto.getIds().isEmpty());
        removeByIds(dto.getIds());
    }

    @Override
    public UserProblemVO detail(Object id) {
        UserProblem userProblem = getById((Serializable) id);
        CommonResponseEnum.INVALID_ID.assertNull(userProblem);
        return BeanCopyUtils.copy(userProblem, UserProblemVO.class);
    }

    @SneakyThrows
    @Override
    public void importExcel(ImportExcelDTO dto) {
        ExcelResult<UserProblemImportDTO> excelResult = ExcelUtils.importExcel(dto.getFile().getInputStream(), UserProblemImportDTO.class, true);
        List<UserProblemImportDTO> list = excelResult.getList();
        List<String> errorList = excelResult.getErrorList();
        String analysis = excelResult.getAnalysis();
        System.out.println(" analysis : " + analysis);
        System.out.println(" isCover : " + dto.getIsCover());
    }

    @SneakyThrows
    @Override
    public void exportExcel(UserProblemListDTO dto, HttpServletResponse response) {
        List<UserProblemVO> list = list(dto);
        String fileName = "用户题目数据模板";
        OutputStream os = FileUtils.getOutputStream(response, fileName + ".xlsx");
        ExcelUtils.exportExcel(list, "用户题目数据", UserProblemVO.class, os);
    }

    @Override
    public UserProblem getByUserAndProblem(Long userId, Long problemId, JudgeStatus status) {
        return mapper.getByUserAndProblem(userId, problemId, status);
    }

    @Override
    public void saveUserProblem(Judge judge) {
        UserProblem userProblem = mapper.getByUserAndProblem(judge.getUserId(), judge.getProblemId(), judge.status());
        if (userProblem != null) {
            return;
        }
        userProblem = new UserProblem();
        userProblem.setUserId(judge.getUserId());
        userProblem.setProblemId(judge.getProblemId());
        userProblem.setProblemKey(judge.getProblemKey());
        userProblem.setProblemTitle(judge.getProblemTitle());
        userProblem.setJudgeId(judge.getId());
        userProblem.setJudgeStatus(judge.getStatus());
        userProblem.setScore(judge.getScore());
        userProblem.setCode(judge.getCode());
        userProblem.setOptions(judge.getReplyOptions());
        userProblem.setFlowImage(judge.getFlowImage());
        userProblem.setProblemType(judge.getProblemType());
        mapper.insert(userProblem, true);
    }
}