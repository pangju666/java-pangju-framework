package io.github.pangju666.framework.core.exception.web.authentication;

import io.github.pangju666.framework.core.exception.web.AuthenticationException;
import org.springframework.http.HttpStatus;

public class NoPermissionException extends AuthenticationException {
	// 无权限状态码应该为403
	public NoPermissionException() {
		super(-42300, "无权限异常", HttpStatus.OK.value());
	}

	public NoPermissionException(String message) {
		super(-42300, message, HttpStatus.OK.value());
	}

	public NoPermissionException(String message, Throwable cause) {
		super(-42300, message, HttpStatus.OK.value(), cause);
	}

	protected NoPermissionException(int code, String message) {
		super(code, message, HttpStatus.OK.value());
	}

	protected NoPermissionException(int code, String message, Throwable cause) {
		super(code, message, HttpStatus.OK.value(), cause);
	}

	protected NoPermissionException(int code, String message, int status) {
		super(code, message, status);
	}

	protected NoPermissionException(int code, String message, int status, Throwable cause) {
		super(code, message, status, cause);
	}
}
