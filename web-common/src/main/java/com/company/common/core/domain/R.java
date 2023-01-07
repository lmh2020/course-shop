package com.company.common.core.domain;

import com.company.common.enums.HttpStatusEnum;
import lombok.Data;
import lombok.Getter;

/**
 * 通用JSON返回对象
 *
 */
@Data
public class R<T> {

    private Integer code;

    private String msg;

    private T data;

    R() {}

    R(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static R success() {
        return create(HttpStatusEnum.SUCCESS, HttpStatusEnum.SUCCESS.getDesc(), null);
    }

    public static R success(String msg) {
        return create(HttpStatusEnum.SUCCESS, msg, null);
    }

    public static <T> R successWithData(T data) {
        return create(HttpStatusEnum.SUCCESS, HttpStatusEnum.SUCCESS.getDesc(), data);
    }

    public static <T> R success(String msg, T data) {
        return create(HttpStatusEnum.SUCCESS, msg, data);
    }

    /* 服务器内部错误，如限流、访问超时、资源异常等 */
    public static R error(String msg) {
        return create(HttpStatusEnum.SERVER_ERROR, msg, null);
    }

    /* 服务器内部错误，如限流、访问超时、资源异常等 */
    public static R error() {
        return create(HttpStatusEnum.SERVER_ERROR, HttpStatusEnum.SERVER_ERROR.getDesc(), null);
    }

    /* 请求错误，如参数错误、数据异常、非法操作等 */
    public static R fail(String msg) {
        return create(HttpStatusEnum.NOT_IMPLEMENTED, msg, null);
    }

    public static R fail(HttpStatusEnum httpStatusEnum, String msg) {
        return create(httpStatusEnum, msg, null);
    }

    public static <T> R create(HttpStatusEnum httpStatusEnum, String msg, T t) {
        return new R(httpStatusEnum.getCode(), msg, t);
    }

    @Getter
    public static class TmpR<T>{

        private Integer tmpCode;

        private String tmpMsg;

        private T tmpData;

        public TmpR success(){
            this.tmpCode = HttpStatusEnum.SUCCESS.getCode();
            return this;
        }

        public R build(){
            return R.successWithData(this.tmpData);
        }

    }

    public static TmpR Builder() {
        return new TmpR();
    }

    public Boolean isSuccess() {
        return HttpStatusEnum.SUCCESS.getCode().equals(this.code);
    }

}
