package io.github.pangju666.framework.core.exception.authentication;

import io.github.pangju666.framework.core.lang.pool.ConstantPool;

public class NoRoleException extends AuthenticationException {
	public NoRoleException(String message) {
		super(message);
		this.setCode(ConstantPool.OK_HTTP_STATUS_CODE);
		this.setHttpStatus(ConstantPool.AUTHENTICATION_EXPIRE_HTTP_STATUS_CODE);
	}

	public NoRoleException(String message, String reason) {
		super(message, reason);
		this.setCode(ConstantPool.OK_HTTP_STATUS_CODE);
		this.setHttpStatus(ConstantPool.AUTHENTICATION_EXPIRE_HTTP_STATUS_CODE);
	}

	public NoRoleException(String message, Throwable cause) {
		super(message, cause);
		this.setCode(ConstantPool.OK_HTTP_STATUS_CODE);
		this.setHttpStatus(ConstantPool.AUTHENTICATION_EXPIRE_HTTP_STATUS_CODE);
	}

	public NoRoleException(String message, String reason, Throwable cause) {
		super(message, reason, cause);
		this.setCode(ConstantPool.OK_HTTP_STATUS_CODE);
		this.setHttpStatus(ConstantPool.AUTHENTICATION_EXPIRE_HTTP_STATUS_CODE);
	}
}
