package io.github.pangju666.framework.web.exception.authentication;

import io.github.pangju666.framework.core.lang.pool.ConstantPool;
import io.github.pangju666.framework.web.exception.AuthenticationException;
import org.springframework.http.HttpStatus;

public class NoRoleException extends AuthenticationException {
	// 无角色状态码应该为403
	public NoRoleException() {
		super(ConstantPool.SECURITY_ERROR_RESPONSE_CODE, "无角色异常", HttpStatus.OK.value());
	}

	public NoRoleException(String message) {
		super(ConstantPool.SECURITY_ERROR_RESPONSE_CODE, message, HttpStatus.OK.value());
	}

	public NoRoleException(String message, Throwable cause) {
		super(ConstantPool.SECURITY_ERROR_RESPONSE_CODE, message, HttpStatus.OK.value(), cause);
	}

	protected NoRoleException(int code, String message) {
		super(code, message, HttpStatus.OK.value());
	}

	protected NoRoleException(int code, String message, Throwable cause) {
		super(code, message, HttpStatus.OK.value(), cause);
	}

	protected NoRoleException(int code, String message, int status) {
		super(code, message, status);
	}

	protected NoRoleException(int code, String message, int status, Throwable cause) {
		super(code, message, status, cause);
	}
}
