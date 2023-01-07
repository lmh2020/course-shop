package com.company.common.exception;


import com.company.common.enums.HttpStatusEnum;
import lombok.Getter;

/**
 * 系统异常
 *
 */
@Getter
public class SystemException extends RuntimeException {

    private HttpStatusEnum code;

    private String message;

    public SystemException(String message) {
        this.message = message;
    }

    public SystemException(String message, HttpStatusEnum code) {
        this.message = message;
        this.code = code;
    }

    public SystemException(String message, Throwable e) {
        super(message, e);
        this.message = message;
    }

    public SystemException(Throwable e) {
        super(e);
        this.message = e.getMessage();
    }

}
