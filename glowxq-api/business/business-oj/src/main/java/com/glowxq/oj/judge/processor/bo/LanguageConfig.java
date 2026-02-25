package com.glowxq.oj.judge.processor.bo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * @Author Himit_ZH
 * @Date 2022/11/22
 */
@Data
public class LanguageConfig {

    /**
     * 语言名称
     */
    private String language;

    /**
     * 源代码文件名称
     */
    private String srcName;

    /**
     * 源代码的可执行文件名称
     */
    private String exeName;

    /**
     * 编译最大cpu运行时间 s
     */
    private Long maxCpuTime;

    /**
     * 编译最大真实运行时间 s
     */
    private Long maxRealTime;

    /**
     * 编译最大运行空间 b
     */
    private Long maxMemory;

    /**
     * 编译命令
     */
    private String compileCommand;

    /**
     * 运行命令
     */
    private String runCommand;

    /**
     * 编译运行环境
     */
    private List<String> compileEnvs;

    /**
     * 执行程序环境
     */
    private List<String> runEnvs;

    /**
     * 是否为c语言
     *
     * @return
     */
    @JsonIgnore
    public Boolean isC() {
        if (StringUtils.isBlank(this.getSrcName())) {
            return false;
        }
        if (StringUtils.endsWith(this.getSrcName(), ".c")) {
            return true;
        }
        if (StringUtils.endsWith(this.getSrcName(), ".cpp")) {
            return true;
        }
        return false;
    }

    @JsonIgnore
    public Boolean isNotC() {
        return !this.isC();
    }
}
