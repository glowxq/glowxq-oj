package com.glowxq.oj.judge.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * <p>
 * JudgeServer添加DTO
 * </p>
 *
 * @author glowxq
 * @since 2025-03-17
 */
@Data
@Schema(description = "JudgeServer添加DTO")
public class JudgeServerCreateDTO {

   @Schema(description =  "判题服务名字")
   private String name;

   @Schema(description =  "判题机ip")
   private String ip;

   @Schema(description =  "判题机端口号")
   private Integer port;

   @Schema(description =  "ip:port")
   private String url;

   @Schema(description =  "判题机所在服务器cpu核心数")
   private Integer cpuCore;

   @Schema(description =  "判题机所在服务器空闲内存")
   private Integer freeMemory;

   @Schema(description =  "当前判题数")
   private Integer taskNumber;

   @Schema(description =  "判题并发最大数")
   private Integer maxTaskNumber;

   @Schema(description =  "0禁用|1启用")
   private Boolean enable;

   @Schema(description =  "是否开启远程判题vj")
   private Boolean remoteEnable;

   @Schema(description =  "是否可提交CF")
   private Boolean cfSubmittableEnable;

}