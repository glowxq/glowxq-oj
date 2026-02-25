package com.glowxq.oj.judge.processor.exception;

import lombok.Data;

/**
 * @Author: Himit_ZH
 * @Date: 2021/1/31 00:17
 * @Description:
 */
@Data
public class SystemException extends Exception {
    private String message;
    private String stdout;
    private String stderr;

    public SystemException(String message, String stdout, String stderr) {
        super(message + " " + stderr);
        this.message = message;
        this.stdout = stdout;
        this.stderr = stderr;
    }

}