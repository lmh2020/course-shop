package com.company.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;


/**
 */

@Getter
@AllArgsConstructor
public enum HttpStatusEnum {

    SUCCESS(200, "请求成功"),
    UNAUTHORIZED(401, "鉴权失败"),
    NOT_LOGIN(402, "用户未登录"),
    FORBIDDEN(403, "请求被拒绝"),
    NOT_FOUND(404, "请求资源未找到"),
    REQUEST_LIMIT(429, "服务器资源紧张"),
    SERVER_ERROR(500, "服务器错误"),
    NOT_IMPLEMENTED(501, "请求错误"),
    BAD_GATEWAY(502, "网关异常"),
    SERVICE_UNAVAILABLE(503, "服务器过载"),

    USER_ACCOUNT_UNUSUAL(1000, "账号状态异常"),

    USER_NOT_FOUND(1001, "用户不存在");


    private Integer code;

    private String desc;

}
