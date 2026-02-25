package com.glowxq.oj.submit.normal.impl;

import com.glowxq.core.common.exception.common.BusinessException;
import com.glowxq.oj.judge.enums.JudgeSceneType;
import com.glowxq.oj.judge.enums.JudgeStatus;
import com.glowxq.oj.judge.pojo.po.Judge;
import com.glowxq.oj.judge.processor.handler.JudgeHandlerImpl;
import com.glowxq.oj.problem.pojo.po.Problem;
import com.glowxq.oj.submit.normal.dto.GlobalNormalSubmitDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * @author glowxq
 * @version 1.0
 * @date 2025/3/19
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ProgramNormalSubmitImpl extends BaseNormalSubmitImpl {

    private final static List<String> LANGUAGE_LIST = Arrays.asList(
            "C++", "C++ With O2", "C++ 17", "C++ 17 With O2", "C++ 20", "C++ 20 With O2",
            "C", "C With O2", "Python3", "Python2", "Java", "Golang", "C#", "PHP", "PyPy2", "PyPy3",
            "JavaScript Node", "JavaScript V8", "Ruby", "Rust");

    private final JudgeHandlerImpl judgeHandler;

    private Judge judge;

    /**
     * 验证判题请求数据合法性
     *
     * <p>执行顺序检查：
     * 1. 语言合规性检查
     * 2. 代码长度有效性检查
     * 3. 流程图强制要求检查
     * 4. 非比赛场景提交频率检查（待实现）
     *
     * @param normalSubmitDTO 待验证的判题请求数据传输对象
     * @throws BusinessException 当遇到以下情况时抛出：
     *                           <ul>
     *                             <li>提交语言不在白名单中（非远程提交时）</li>
     *                             <li>代码长度低于50字符（特定语言除外）</li>
     *                             <li>代码长度超过65535字符</li>
     *                             <li>题目要求流程图但未提交</li>
     *                             <li>指定题目不存在</li>
     *                           </ul>
     */
    @Override
    protected void checkJudgeDto(GlobalNormalSubmitDTO normalSubmitDTO) {
        super.checkJudgeDto(normalSubmitDTO);
        if (!LANGUAGE_LIST.contains(normalSubmitDTO.getLanguage())) {
            throw new BusinessException("提交的代码的语言错误！请使用" + LANGUAGE_LIST + "中之一的语言！");
        }
        // 语言白名单（示例值，需根据实际配置调整）
        final List<String> HOJ_LANGUAGE_LIST = Arrays.asList("Java", "Python", "C++");
        // 允许短代码的语言标识（包含即视为允许）
        final List<String> ALLOW_SHORT_CODE_LANGUAGES = Arrays.asList("Py", "PHP", "Ruby", "Rust", "JavaScript");
        // 代码长度限制
        final int MIN_CODE_LENGTH = 50;
        final int MAX_CODE_LENGTH = 65535;

        /* 语言合规性验证 */
        if (!normalSubmitDTO.getIsRemote() && !HOJ_LANGUAGE_LIST.contains(normalSubmitDTO.getLanguage())) {
            throw new BusinessException(String.format("非法提交语言[%s]，允许的语言列表：%s", normalSubmitDTO.getLanguage(), HOJ_LANGUAGE_LIST));
        }

        /* 代码长度有效性验证 */
        int codeLength = normalSubmitDTO.getCode().length();
        // 检测是否属于允许短代码的语言
        boolean isShortAllowed = ALLOW_SHORT_CODE_LANGUAGES.stream()
                                                           .anyMatch(lang -> normalSubmitDTO.getLanguage().contains(lang));

        if (codeLength < MIN_CODE_LENGTH && !isShortAllowed) {
            throw new BusinessException(String.format("代码长度不足：当前%d字符，最低要求%d字符（%s语言除外）", codeLength, MIN_CODE_LENGTH, ALLOW_SHORT_CODE_LANGUAGES));
        }

        if (codeLength > MAX_CODE_LENGTH) {
            throw new BusinessException(String.format("代码超长：当前%d字符，最大允许%d字符", codeLength, MAX_CODE_LENGTH));
        }

        /* 题目元数据验证 */
        Problem problem = problemService.getById(normalSubmitDTO.getProblemId());
        if (problem == null) {
            throw new BusinessException("无效题目ID：" + normalSubmitDTO.getProblemId());
        }

        // 流程图强制要求检查
        if (problem.getRequireImage() && StringUtils.isBlank(normalSubmitDTO.getFlowImage())) {
            throw new BusinessException("题目[" + problem.getTitle() + "]必须提交流程图");
        }

        /* 非比赛提交频率控制（待实现） */
        if (!JudgeSceneType.Contest.equals(normalSubmitDTO.getJudgeSceneType())) {
            // TODO 实现频率限制逻辑
        }
    }

    @Override
    protected Judge preparationData(GlobalNormalSubmitDTO judgeDto) {
        this.judge = baseSaveSubmit(judgeDto);
        return judge;
    }

    @Override
    protected void submitExceptionHandler(Judge judge, Exception e) {
        super.submitExceptionHandler(judge, e);
        this.judge.setStatus(JudgeStatus.STATUS_SUBMITTED_FAILED.getStatus());
        judgeService.updateById(this.judge);
    }
}
