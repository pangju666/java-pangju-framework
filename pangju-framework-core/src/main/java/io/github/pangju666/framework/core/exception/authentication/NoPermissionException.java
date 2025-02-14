package io.github.pangju666.framework.core.exception.authentication;

import io.github.pangju666.framework.core.lang.pool.Constants;

public class NoPermissionException extends AuthenticationException {
	public NoPermissionException(String message) {
		super(Constants.AUTHENTICATION_ERROR_RESPONSE_CODE, message);
		this.setHttpStatus(Constants.FORBIDDEN_HTTP_STATUS_CODE);
		this.setLog(false);
	}

	public NoPermissionException(String message, String reason) {
		super(Constants.AUTHENTICATION_ERROR_RESPONSE_CODE, message, reason);
		this.setHttpStatus(Constants.FORBIDDEN_HTTP_STATUS_CODE);
	}

	public NoPermissionException(String message, Throwable cause) {
		super(Constants.AUTHENTICATION_ERROR_RESPONSE_CODE, message, cause);
		this.setHttpStatus(Constants.FORBIDDEN_HTTP_STATUS_CODE);
		this.setLog(false);
	}

	public NoPermissionException(String message, String reason, Throwable cause) {
		super(Constants.AUTHENTICATION_ERROR_RESPONSE_CODE, message, reason, cause);
		this.setHttpStatus(Constants.FORBIDDEN_HTTP_STATUS_CODE);
	}
}