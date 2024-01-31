package io.github.pangju666.framework.core.exception.web.authentication;

import io.github.pangju666.framework.core.exception.web.AuthenticationException;
import org.springframework.http.HttpStatus;

public class AuthenticationExpireException extends AuthenticationException {
	public AuthenticationExpireException() {
		super(-42100, "身份认证已过期", HttpStatus.FORBIDDEN.value());
	}

	public AuthenticationExpireException(String message) {
		super(-42100, message, HttpStatus.FORBIDDEN.value());
	}

	public AuthenticationExpireException(String message, Throwable cause) {
		super(-42100, message, HttpStatus.FORBIDDEN.value(), cause);
	}

	protected AuthenticationExpireException(int code, String message) {
		super(code, message, HttpStatus.FORBIDDEN.value());
	}

	protected AuthenticationExpireException(int code, String message, Throwable cause) {
		super(code, message, HttpStatus.FORBIDDEN.value(), cause);
	}

	protected AuthenticationExpireException(int code, String message, int status) {
		super(code, message, status);
	}

	protected AuthenticationExpireException(int code, String message, int status, Throwable cause) {
		super(code, message, status, cause);
	}
}
