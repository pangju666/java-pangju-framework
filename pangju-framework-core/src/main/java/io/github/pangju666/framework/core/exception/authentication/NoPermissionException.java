package io.github.pangju666.framework.core.exception.authentication;

import io.github.pangju666.framework.core.lang.pool.ConstantPool;

public class NoPermissionException extends AuthenticationException {
	public NoPermissionException(String message) {
		super(message);
		this.setCode(ConstantPool.AUTHENTICATION_ERROR_RESPONSE_CODE);
		this.setHttpStatus(ConstantPool.FORBIDDEN_HTTP_STATUS_CODE);
	}

	public NoPermissionException(String message, String reason) {
		super(message, reason);
		this.setCode(ConstantPool.AUTHENTICATION_ERROR_RESPONSE_CODE);
		this.setHttpStatus(ConstantPool.FORBIDDEN_HTTP_STATUS_CODE);
	}

	public NoPermissionException(String message, Throwable cause) {
		super(message, cause);
		this.setCode(ConstantPool.AUTHENTICATION_ERROR_RESPONSE_CODE);
		this.setHttpStatus(ConstantPool.FORBIDDEN_HTTP_STATUS_CODE);
	}

	public NoPermissionException(String message, String reason, Throwable cause) {
		super(message, reason, cause);
		this.setCode(ConstantPool.AUTHENTICATION_ERROR_RESPONSE_CODE);
		this.setHttpStatus(ConstantPool.FORBIDDEN_HTTP_STATUS_CODE);
	}
}
