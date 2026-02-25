package com.glowxq.oj.judge.processor.exception;

import lombok.Data;

/**
 * @Author: Himit_ZH
 * @Date: 2021/4/16 13:52
 * @Description:
 */
@Data
public class SubmitException extends Exception {
    private String message;
    private String stdout;
    private String stderr;

    public SubmitException(String message, String stdout, String stderr) {
        super(message);
        this.message = message;
        this.stdout = stdout;
        this.stderr = stderr;
    }
}