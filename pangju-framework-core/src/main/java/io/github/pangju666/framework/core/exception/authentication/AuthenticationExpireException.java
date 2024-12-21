package io.github.pangju666.framework.core.exception.authentication;

import io.github.pangju666.framework.core.lang.pool.Constants;

public class AuthenticationExpireException extends AuthenticationException {
	public AuthenticationExpireException(String message) {
		super(Constants.AUTHENTICATION_EXPIRE_ERROR_RESPONSE_CODE, message);
		this.setHttpStatus(Constants.UNAUTHORIZED_HTTP_STATUS_CODE);
	}

	public AuthenticationExpireException(String message, String reason) {
		super(Constants.AUTHENTICATION_EXPIRE_ERROR_RESPONSE_CODE, message, reason);
		this.setHttpStatus(Constants.UNAUTHORIZED_HTTP_STATUS_CODE);
	}

	public AuthenticationExpireException(String message, Throwable cause) {
		super(Constants.AUTHENTICATION_EXPIRE_ERROR_RESPONSE_CODE, message, cause);
		this.setHttpStatus(Constants.UNAUTHORIZED_HTTP_STATUS_CODE);
	}

	public AuthenticationExpireException(String message, String reason, Throwable cause) {
		super(Constants.AUTHENTICATION_EXPIRE_ERROR_RESPONSE_CODE, message, reason, cause);
		this.setHttpStatus(Constants.UNAUTHORIZED_HTTP_STATUS_CODE);
	}
}