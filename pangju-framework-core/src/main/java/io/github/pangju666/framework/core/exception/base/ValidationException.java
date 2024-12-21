package io.github.pangju666.framework.core.exception.base;


import io.github.pangju666.framework.core.lang.pool.Constants;
import org.slf4j.Logger;
import org.slf4j.event.Level;

public class ValidationException extends BaseRuntimeException {
	public ValidationException(String message) {
		super(Constants.VALIDATION_ERROR_RESPONSE_CODE, message);
		this.setHttpStatus(Constants.BAD_REQUEST_HTTP_STATUS_CODE);
	}

	public ValidationException(String message, Throwable cause) {
		super(Constants.VALIDATION_ERROR_RESPONSE_CODE, message, cause);
		this.setHttpStatus(Constants.BAD_REQUEST_HTTP_STATUS_CODE);
	}

	protected ValidationException(int code, String message) {
		super(code, message);
		this.setHttpStatus(Constants.BAD_REQUEST_HTTP_STATUS_CODE);
	}

	protected ValidationException(int code, String message, Throwable cause) {
		super(code, message, cause);
		this.setHttpStatus(Constants.BAD_REQUEST_HTTP_STATUS_CODE);
	}

	@Override
	public void log(Logger logger) {
	}

	@Override
	public void log(Logger logger, Level level) {
	}
}