package com.glowxq.system.admin.pojo.dto.sysfile;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class GeneratePreUrlDTO {

    /**
     * 文件名
     */
    @NotBlank(message = "文件名不能爲空")
    private String name;
}
