package com.company.common.exception;


import com.company.common.enums.HttpStatusEnum;
import lombok.Getter;

/**
 * 业务异常
 *
 */
@Getter
public class ServiceException extends RuntimeException {

    private HttpStatusEnum code;

    private String message;

    public ServiceException(String message) {
        this.message = message;
    }

    public ServiceException(String message, HttpStatusEnum code) {
        this.message = message;
        this.code = code;
    }

    public ServiceException(String message, Throwable e) {
        super(message, e);
        this.message = message;
    }

}
