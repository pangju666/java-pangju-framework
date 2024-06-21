package io.github.pangju666.framework.core.exception.authentication;

import io.github.pangju666.framework.core.exception.base.ServiceException;
import io.github.pangju666.framework.core.lang.pool.ConstantPool;
import org.slf4j.Logger;
import org.slf4j.event.Level;

public class AuthenticationException extends ServiceException {
    public AuthenticationException(String message) {
        super(ConstantPool.AUTHENTICATION_ERROR_RESPONSE_CODE, message);
        this.setHttpStatus(ConstantPool.UNAUTHORIZED_HTTP_STATUS_CODE);
    }

    public AuthenticationException(String message, String reason) {
        super(ConstantPool.AUTHENTICATION_ERROR_RESPONSE_CODE, message, reason);
        this.setHttpStatus(ConstantPool.UNAUTHORIZED_HTTP_STATUS_CODE);
    }

    public AuthenticationException(String message, Throwable cause) {
        super(ConstantPool.AUTHENTICATION_ERROR_RESPONSE_CODE, message, cause);
        this.setHttpStatus(ConstantPool.UNAUTHORIZED_HTTP_STATUS_CODE);
    }

    public AuthenticationException(String message, String reason, Throwable cause) {
        super(ConstantPool.AUTHENTICATION_ERROR_RESPONSE_CODE, message, reason, cause);
        this.setHttpStatus(ConstantPool.UNAUTHORIZED_HTTP_STATUS_CODE);
    }

    protected AuthenticationException(int code, String message) {
        super(code, message);
        this.setHttpStatus(ConstantPool.UNAUTHORIZED_HTTP_STATUS_CODE);
    }

    protected AuthenticationException(int code, String message, String reason) {
        super(code, message, reason);
        this.setHttpStatus(ConstantPool.UNAUTHORIZED_HTTP_STATUS_CODE);
    }

    protected AuthenticationException(int code, String message, Throwable cause) {
        super(code, message, cause);
        this.setHttpStatus(ConstantPool.UNAUTHORIZED_HTTP_STATUS_CODE);
    }

    protected AuthenticationException(int code, String message, String reason, Throwable cause) {
        super(code, message, reason, cause);
        this.setHttpStatus(ConstantPool.UNAUTHORIZED_HTTP_STATUS_CODE);
    }

    @Override
    public void log(Logger logger) {
    }

    @Override
    public void log(Logger logger, Level level) {
    }
}
