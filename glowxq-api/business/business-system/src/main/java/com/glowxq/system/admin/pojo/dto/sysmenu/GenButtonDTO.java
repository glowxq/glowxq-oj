package com.glowxq.system.admin.pojo.dto.sysmenu;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class GenButtonDTO {

    @Schema(description = "选择的标识数组")
    @Size(min = 1)
    private List<String> ids;

    @NotBlank
    private String code;
}
