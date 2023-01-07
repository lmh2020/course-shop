package com.company.common.exception;

import com.company.common.enums.HttpStatusEnum;

/**
 * 自定义异常
 *
 */
public class CustomException extends RuntimeException {

    private HttpStatusEnum status;

    private String message;

    public CustomException(String message) {
        this.message = message;
    }

    public CustomException(String message, HttpStatusEnum status) {
        this.message = message;
        this.status = status;
    }

    public CustomException(String message, Throwable e) {
        super(message, e);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public HttpStatusEnum getStatus() {
        return status;
    }
}
