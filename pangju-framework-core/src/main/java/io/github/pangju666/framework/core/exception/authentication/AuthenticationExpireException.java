package io.github.pangju666.framework.core.exception.authentication;

import io.github.pangju666.framework.core.lang.pool.ConstantPool;

public class AuthenticationExpireException extends AuthenticationException {
	public AuthenticationExpireException(String message) {
		super(message);
		this.setCode(ConstantPool.AUTHENTICATION_EXPIRE_ERROR_RESPONSE_CODE);
	}

	public AuthenticationExpireException(String message, String reason) {
		super(message, reason);
		this.setCode(ConstantPool.AUTHENTICATION_EXPIRE_ERROR_RESPONSE_CODE);
	}

	public AuthenticationExpireException(String message, Throwable cause) {
		super(message, cause);
		this.setCode(ConstantPool.AUTHENTICATION_EXPIRE_ERROR_RESPONSE_CODE);
	}

	public AuthenticationExpireException(String message, String reason, Throwable cause) {
		super(message, reason, cause);
		this.setCode(ConstantPool.AUTHENTICATION_EXPIRE_ERROR_RESPONSE_CODE);
	}
}
