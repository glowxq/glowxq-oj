package com.glowxq.oj.user.pojo.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * @author glowxq
 * @version 1.0
 * @date 2025/6/23
 */
@Data
public class UserInfoBindDTO {

    @NotNull
    @Size(min = 1,max = 10)
    private List<Long> userIds;

    @NotNull
    @Size(min = 1,max = 10)
    private List<Long> groupIds;
}
