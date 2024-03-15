package io.github.pangju666.framework.web.exception;

import io.github.pangju666.framework.core.exception.base.ServiceException;
import io.github.pangju666.framework.core.lang.pool.ConstantPool;
import org.springframework.http.HttpStatus;

public class AuthenticationException extends ServiceException {
	public AuthenticationException() {
		super(ConstantPool.SECURITY_ERROR_RESPONSE_CODE, "身份认证异常", HttpStatus.UNAUTHORIZED.value());
	}

	public AuthenticationException(String message) {
		super(ConstantPool.SECURITY_ERROR_RESPONSE_CODE, message, HttpStatus.UNAUTHORIZED.value());
	}

	public AuthenticationException(String message, Throwable cause) {
		super(ConstantPool.SECURITY_ERROR_RESPONSE_CODE, message, HttpStatus.UNAUTHORIZED.value(), cause);
	}

	protected AuthenticationException(int code, String message) {
		super(code, message, HttpStatus.UNAUTHORIZED.value());
	}

	protected AuthenticationException(int code, String message, Throwable cause) {
		super(code, message, HttpStatus.UNAUTHORIZED.value(), cause);
	}

	protected AuthenticationException(int code, String message, int status) {
		super(code, message, status);
	}

	protected AuthenticationException(int code, String message, int status, Throwable cause) {
		super(code, message, status, cause);
	}
}
