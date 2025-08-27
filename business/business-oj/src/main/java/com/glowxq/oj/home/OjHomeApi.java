package com.glowxq.oj.home;

import com.glowxq.core.common.api.BaseApi;
import com.glowxq.core.common.entity.ApiResult;
import com.glowxq.core.common.exception.common.BusinessException;
import com.glowxq.core.util.StringUtils;
import com.glowxq.oj.common.enums.RedisKeyEnum;
import com.glowxq.oj.course.service.CourseService;

import com.glowxq.oj.home.vo.GlobalDataCountVO;
import com.glowxq.oj.home.vo.ProblemGroupCountVO;
import com.glowxq.oj.judge.service.JudgeService;
import com.glowxq.oj.problem.pojo.po.Problem;
import com.glowxq.oj.problem.service.ProblemService;
import com.glowxq.oj.topic.pojo.po.Topic;
import com.glowxq.oj.topic.service.TopicService;
import com.glowxq.oj.user.pojo.po.UserInfo;
import com.glowxq.oj.user.service.UserInfoService;
import com.glowxq.oj.home.vo.CheckinResultVO;
import com.glowxq.redis.RedisUtils;
import com.glowxq.security.core.util.LoginUtils;
import com.mybatisflex.core.query.QueryWrapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;


import static com.glowxq.oj.problem.pojo.po.table.ProblemTableDef.PROBLEM;
import static com.mybatisflex.core.query.QueryMethods.count;


/**
 * @author glowxq
 * @version 1.0
 * @date 2025/3/28
 */
@Tag(name = "首页")
@RestController
@RequestMapping
@RequiredArgsConstructor
public class OjHomeApi extends BaseApi {

    private final TopicService topicService;

    private final JudgeService judgeService;



    private final CourseService courseService;

    private final ProblemService problemService;

    private final UserInfoService userInfoService;

    /**
     * 获取全局统计
     *
     * @return
     */
    @GetMapping("/home/data/getGlobalDataCount")
    public ApiResult<GlobalDataCountVO> getGlobalDataCount() {
        GlobalDataCountVO globalDataCountVO = new GlobalDataCountVO();
        globalDataCountVO.setUserCount(userInfoService.count());
        globalDataCountVO.setTopicCount(topicService.count());
        globalDataCountVO.setProblemCount(problemService.count());
        globalDataCountVO.setAcCount(judgeService.countAc());
        globalDataCountVO.setJudgeCount(judgeService.count());
        globalDataCountVO.setGroupCount(0L);
        globalDataCountVO.setCourseCount(courseService.count());
        return ApiResult.success(globalDataCountVO);
    }

    /**
     * 统计题型数据
     *
     * @return
     */
    @GetMapping("/home/data/countProblemTypeData")
    public ApiResult<List<ProblemGroupCountVO>> countProblemTypeData() {
        QueryWrapper queryWrapper = QueryWrapper.create();
        queryWrapper.select(PROBLEM.PROBLEM_TYPE, count(PROBLEM.ALL_COLUMNS).as("number")).from(Problem.class).groupBy(Problem::getProblemType);

        List<ProblemGroupCountVO> problemGroupCountVOList = problemService.getMapper().selectListByQueryAs(queryWrapper, ProblemGroupCountVO.class);
        return ApiResult.success(problemGroupCountVOList);
    }

    /**
     * 获取用户详情
     *
     * @return
     */
    @GetMapping("/home/getUserDetail")
    public ApiResult<UserInfo> getUserDetail() {
        UserInfo userInfo = userInfoService.getById(LoginUtils.getUserId());
        return ApiResult.success(userInfo);
    }

    /**
     * 全局排名
     *
     * @return
     */
    @GetMapping("/home/rankGlobal")
    public ApiResult<List<UserInfo>> rankGlobal() {
        List<UserInfo> userInfos = userInfoService.acRankingList();
        return ApiResult.success(userInfos);
    }

    /**
     * 首页展示的比赛
     *
     * @return
     */
    @GetMapping("/home/homeTopic")
    public ApiResult<List<Topic>> homeTopic(@RequestParam(defaultValue = "3") Integer num) {
        List<Topic> topicList = topicService.homeTopic(num);
        return ApiResult.success(topicList);
    }


    /**
     * 用户签到
     *
     * @return
     */
    @PostMapping("/home/sign")
    public ApiResult<CheckinResultVO> sign() {
        Long userId = LoginUtils.getUserId();
        UserInfo userInfo = userInfoService.getById(userId);

        // 获取用户最后一次签到日期
        String redisKey = RedisKeyEnum.LastSignDay.buildKey(userId);
        String lastCheckInDateObj = Optional.ofNullable(RedisUtils.getValue(redisKey)).map(Object::toString).orElse("");

        LocalDate today = LocalDate.now();
        String todayStr = today.toString();

        // 检查今日是否已签到
        if (StringUtils.equals(todayStr, lastCheckInDateObj)) {
            throw new BusinessException("今日已签到");
        }

        // 获取用户信息
        Integer continuousSignDay = userInfo.getContinuousSignDay();

        // 判断连续签到逻辑
        LocalDate yesterday = today.minusDays(1);
        boolean isConsecutive = yesterday.toString().equals(lastCheckInDateObj);

        if (isConsecutive) {
            continuousSignDay += 1;
        }
        else {
            // 非连续签到则重置为1（包含首次签到）
            continuousSignDay = 1;
        }

        // 基础奖励分
        int baseIntegral = 15;

        // 计算奖励积分
        int continuousIntegral = Math.min(continuousSignDay, 30);
        int signAddIntegral = baseIntegral + continuousIntegral;

        // 更新用户积分及签到天数
        userInfo.addIntegral(signAddIntegral);
        userInfo.setContinuousSignDay(continuousSignDay);
        userInfoService.updateById(userInfo);

        // 设置Redis缓存（记录今日签到，并设置次日23点59分59秒过期）
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime tomorrowEndOfDay = now.plusDays(1)
                                            .toLocalDate()
                                            .atTime(23, 59, 59);

        long ttlSeconds = Duration.between(now, tomorrowEndOfDay).getSeconds();
        RedisUtils.setValue(redisKey, todayStr, ttlSeconds, TimeUnit.SECONDS);

        // 构造返回结果
        CheckinResultVO result = new CheckinResultVO();
        result.setSignDayIntegral(signAddIntegral);
        result.setTotalIntegral(userInfo.getIntegral());
        result.setContinuousSignDay(continuousSignDay);

        return ApiResult.success(result);
    }
}
