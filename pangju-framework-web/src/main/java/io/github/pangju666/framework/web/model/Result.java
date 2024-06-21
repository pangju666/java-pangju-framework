package io.github.pangju666.framework.web.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.github.pangju666.commons.lang.utils.JsonUtils;
import io.github.pangju666.framework.core.exception.base.BaseRuntimeException;
import io.github.pangju666.framework.core.lang.pool.ConstantPool;

@JsonInclude(value = JsonInclude.Include.NON_NULL)
public record Result<T>(
        String message,
        Integer code,
        T data) {
    public static Result<Void> ok() {
        return new Result<>(ConstantPool.RESPONSE_SUCCESS_MESSAGE, ConstantPool.SUCCESS_RESPONSE_CODE, null);
    }

    public static Result<Void> okByMessage(String message) {
        return new Result<>(message, ConstantPool.SUCCESS_RESPONSE_CODE, null);
    }

    public static <T> Result<T> ok(T data) {
        return new Result<>(ConstantPool.RESPONSE_SUCCESS_MESSAGE, ConstantPool.SUCCESS_RESPONSE_CODE, data);
    }

    public static <E extends BaseRuntimeException> Result<Void> failByException(E e) {
        return new Result<>(e.getMessage(), e.getCode(), null);
    }

    public static Result<Void> fail() {
        return new Result<>(ConstantPool.RESPONSE_FAILURE_MESSAGE, ConstantPool.BASE_ERROR_RESPONSE_CODE, null);
    }

    public static Result<Void> failByMessage(String message) {
        return new Result<>(message, ConstantPool.BASE_ERROR_RESPONSE_CODE, null);
    }

    public static Result<Void> fail(int code, String message) {
        return new Result<>(message, code, null);
    }

    public static <T> Result<T> fail(T data) {
        return new Result<>(ConstantPool.RESPONSE_FAILURE_MESSAGE, ConstantPool.BASE_ERROR_RESPONSE_CODE, data);
    }

    public static <T> Result<T> fail(int code, String message, T data) {
        return new Result<>(message, code, data);
    }

    @Override
    public String toString() {
        return JsonUtils.toString(this);
    }
}