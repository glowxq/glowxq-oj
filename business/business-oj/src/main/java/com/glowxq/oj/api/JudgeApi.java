package com.glowxq.oj.api;

import com.glowxq.core.common.entity.ApiResult;
import com.glowxq.oj.judge.pojo.po.Judge;
import com.glowxq.oj.submit.normal.dto.param.RemoteNormalSubmitDTO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PutExchange;

/**
 *  judge 对外接口
 * @author weiweicode
 * @version 1.0
 * @since 2025/6/13 16:43
 */

@HttpExchange(url = "/judge",accept = "application/json",contentType = "application/json")
public interface JudgeApi {

    @PutExchange(url = "/updateById")
    ApiResult<Void> updateById(@RequestBody Judge judge);

    @PostMapping("/submit/remoteSubmitNormal")
    ApiResult<Judge> remoteSubmitNormal(@RequestBody RemoteNormalSubmitDTO dto);
}