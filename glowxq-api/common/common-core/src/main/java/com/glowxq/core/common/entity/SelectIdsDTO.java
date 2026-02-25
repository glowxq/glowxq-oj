package com.glowxq.core.common.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SelectIdsDTO {

    @Schema(description = "选择的标识数组")
    private List<? extends Serializable> ids = new ArrayList<>();

    public List<String> getStringIds() {
        return ids.stream().map(String::valueOf).collect(Collectors.toList());
    }

    public List<Long> getIds() {
        return ids.stream().map(id-> Long.parseLong(id.toString())).collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return "SelectIdsDTO{" +
                "ids=" + ids +
                '}';
    }
}
