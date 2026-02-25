package com.glowxq.test;

import cn.dev33.satoken.annotation.SaIgnore;
import com.glowxq.core.common.api.BaseApi;
import com.glowxq.core.common.entity.ApiResult;
import com.glowxq.system.admin.pojo.dto.sysuser.UserDeptDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author glowxq
 * @version 1.0
 * @date 2025/8/20
 */
@RestController
@Slf4j
public class TestApi extends BaseApi {

    @SaIgnore
    @RequestMapping("/system/test")
    public ApiResult<UserDeptDTO> test(@RequestBody UserDeptDTO userDeptDTO) {
        log.info("userDeptDTO -========: {}", userDeptDTO);
        return ApiResult.success(userDeptDTO);
    }
}
