package io.github.pangju666.framework.core.exception.authentication;

import io.github.pangju666.framework.core.lang.pool.ConstantPool;

public class NoPermissionException extends AuthenticationException {
    public NoPermissionException(String message) {
        super(ConstantPool.AUTHENTICATION_ERROR_RESPONSE_CODE, message);
        this.setHttpStatus(ConstantPool.FORBIDDEN_HTTP_STATUS_CODE);
    }

    public NoPermissionException(String message, String reason) {
        super(ConstantPool.AUTHENTICATION_ERROR_RESPONSE_CODE, message, reason);
        this.setHttpStatus(ConstantPool.FORBIDDEN_HTTP_STATUS_CODE);
    }

    public NoPermissionException(String message, Throwable cause) {
        super(ConstantPool.AUTHENTICATION_ERROR_RESPONSE_CODE, message, cause);
        this.setHttpStatus(ConstantPool.FORBIDDEN_HTTP_STATUS_CODE);
    }

    public NoPermissionException(String message, String reason, Throwable cause) {
        super(ConstantPool.AUTHENTICATION_ERROR_RESPONSE_CODE, message, reason, cause);
        this.setHttpStatus(ConstantPool.FORBIDDEN_HTTP_STATUS_CODE);
    }
}
