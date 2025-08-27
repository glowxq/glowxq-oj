package com.glowxq.oj.judge.pojo.vo;

import cn.idev.excel.annotation.ExcelIgnore;
import cn.idev.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * <p>
 * JudgeServer返回vo
 * </p>
 *
 * @author glowxq
 * @since 2025-03-17
 */
@Data
@Schema(description = "JudgeServer返回vo")
public class JudgeServerVO {

    @ExcelIgnore
    @Schema(description =  "ID")
    private Long id;

    @ExcelProperty(value = "判题服务名字")
    @Schema(description =  "判题服务名字")
    private String name;

    @ExcelProperty(value = "判题机ip")
    @Schema(description =  "判题机ip")
    private String ip;

    @ExcelProperty(value = "判题机端口号")
    @Schema(description =  "判题机端口号")
    private Integer port;

    @ExcelProperty(value = "ip:port")
    @Schema(description =  "ip:port")
    private String url;

    @ExcelProperty(value = "判题机所在服务器cpu核心数")
    @Schema(description =  "判题机所在服务器cpu核心数")
    private Integer cpuCore;

    @ExcelProperty(value = "判题机所在服务器空闲内存")
    @Schema(description =  "判题机所在服务器空闲内存")
    private Integer freeMemory;

    @ExcelProperty(value = "当前判题数")
    @Schema(description =  "当前判题数")
    private Integer taskNumber;

    @ExcelProperty(value = "判题并发最大数")
    @Schema(description =  "判题并发最大数")
    private Integer maxTaskNumber;

    @ExcelProperty(value = "0禁用|1启用")
    @Schema(description =  "0禁用|1启用")
    private Boolean enable;

    @ExcelProperty(value = "是否开启远程判题vj")
    @Schema(description =  "是否开启远程判题vj")
    private Boolean remoteEnable;

    @ExcelProperty(value = "是否可提交CF")
    @Schema(description =  "是否可提交CF")
    private Boolean cfSubmittableEnable;

}