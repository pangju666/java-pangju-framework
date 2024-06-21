package io.github.pangju666.framework.core.exception.authentication;

import io.github.pangju666.framework.core.lang.pool.ConstantPool;

public class AuthenticationExpireException extends AuthenticationException {
    public AuthenticationExpireException(String message) {
        super(ConstantPool.AUTHENTICATION_EXPIRE_ERROR_RESPONSE_CODE, message);
        this.setHttpStatus(ConstantPool.UNAUTHORIZED_HTTP_STATUS_CODE);
    }

    public AuthenticationExpireException(String message, String reason) {
        super(ConstantPool.AUTHENTICATION_EXPIRE_ERROR_RESPONSE_CODE, message, reason);
        this.setHttpStatus(ConstantPool.UNAUTHORIZED_HTTP_STATUS_CODE);
    }

    public AuthenticationExpireException(String message, Throwable cause) {
        super(ConstantPool.AUTHENTICATION_EXPIRE_ERROR_RESPONSE_CODE, message, cause);
        this.setHttpStatus(ConstantPool.UNAUTHORIZED_HTTP_STATUS_CODE);
    }

    public AuthenticationExpireException(String message, String reason, Throwable cause) {
        super(ConstantPool.AUTHENTICATION_EXPIRE_ERROR_RESPONSE_CODE, message, reason, cause);
        this.setHttpStatus(ConstantPool.UNAUTHORIZED_HTTP_STATUS_CODE);
    }
}
