package io.github.pangju666.framework.core.exception.authentication;

import io.github.pangju666.framework.core.lang.pool.ConstantPool;

public class AuthenticationExpireException extends AuthenticationException {
	public AuthenticationExpireException(String message) {
		super(message);
		this.setReason(message);
		this.setCode(ConstantPool.AUTHENTICATION_EXPIRE_ERROR_RESPONSE_CODE);
		this.setHttpStatus(ConstantPool.AUTHENTICATION_EXPIRE_HTTP_STATUS_CODE);
	}

	public AuthenticationExpireException(String message, String reason) {
		super(message, reason);
		this.setReason(message);
		this.setCode(ConstantPool.AUTHENTICATION_EXPIRE_ERROR_RESPONSE_CODE);
		this.setHttpStatus(ConstantPool.AUTHENTICATION_EXPIRE_HTTP_STATUS_CODE);
	}

	public AuthenticationExpireException(String message, Throwable cause) {
		super(message, cause);
		this.setReason(message);
		this.setCode(ConstantPool.AUTHENTICATION_EXPIRE_ERROR_RESPONSE_CODE);
		this.setHttpStatus(ConstantPool.AUTHENTICATION_EXPIRE_HTTP_STATUS_CODE);
	}

	public AuthenticationExpireException(String message, String reason, Throwable cause) {
		super(message, reason, cause);
		this.setReason(message);
		this.setCode(ConstantPool.AUTHENTICATION_EXPIRE_ERROR_RESPONSE_CODE);
		this.setHttpStatus(ConstantPool.AUTHENTICATION_EXPIRE_HTTP_STATUS_CODE);
	}
}
