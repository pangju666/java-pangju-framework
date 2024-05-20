package io.github.pangju666.framework.core.exception.authentication;

import io.github.pangju666.framework.core.exception.base.ServiceException;
import io.github.pangju666.framework.core.lang.pool.ConstantPool;

public class AuthenticationException extends ServiceException {
	public AuthenticationException(String message) {
		super(message);
		this.setCode(ConstantPool.AUTHENTICATION_ERROR_RESPONSE_CODE);
		this.setHttpStatus(ConstantPool.AUTHENTICATION_HTTP_STATUS_CODE);
	}

	public AuthenticationException(String message, String reason) {
		super(message, reason);
		this.setCode(ConstantPool.AUTHENTICATION_ERROR_RESPONSE_CODE);
		this.setHttpStatus(ConstantPool.AUTHENTICATION_HTTP_STATUS_CODE);
	}

	public AuthenticationException(String message, Throwable cause) {
		super(message, cause);
		this.setCode(ConstantPool.AUTHENTICATION_ERROR_RESPONSE_CODE);
		this.setHttpStatus(ConstantPool.AUTHENTICATION_HTTP_STATUS_CODE);
	}

	public AuthenticationException(String message, String reason, Throwable cause) {
		super(message, reason, cause);
		this.setCode(ConstantPool.AUTHENTICATION_ERROR_RESPONSE_CODE);
		this.setHttpStatus(ConstantPool.AUTHENTICATION_HTTP_STATUS_CODE);
	}
}
