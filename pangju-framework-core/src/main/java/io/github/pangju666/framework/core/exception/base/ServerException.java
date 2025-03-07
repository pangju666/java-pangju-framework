package io.github.pangju666.framework.core.exception.base;

import io.github.pangju666.framework.core.lang.pool.Constants;
import org.slf4j.Logger;
import org.slf4j.event.Level;

public class ServerException extends BaseRuntimeException {
	protected static final String DEFAULT_MESSAGE = "服务器内部错误";

	private final String reason;

	public ServerException() {
		super(Constants.SERVER_ERROR_RESPONSE_CODE, DEFAULT_MESSAGE);
		this.reason = DEFAULT_MESSAGE;
		this.setHttpStatus(Constants.INTERNAL_SERVER_ERROR_HTTP_STATUS_CODE);
	}

	public ServerException(String reason) {
		super(Constants.SERVER_ERROR_RESPONSE_CODE, DEFAULT_MESSAGE);
		this.reason = reason;
		this.setHttpStatus(Constants.INTERNAL_SERVER_ERROR_HTTP_STATUS_CODE);
	}

	public ServerException(Throwable cause) {
		super(Constants.SERVER_ERROR_RESPONSE_CODE, DEFAULT_MESSAGE, cause);
		this.reason = DEFAULT_MESSAGE;
		this.setHttpStatus(Constants.INTERNAL_SERVER_ERROR_HTTP_STATUS_CODE);
	}

	public ServerException(String reason, Throwable cause) {
		super(Constants.SERVER_ERROR_RESPONSE_CODE, DEFAULT_MESSAGE, cause);
		this.reason = reason;
		this.setHttpStatus(Constants.INTERNAL_SERVER_ERROR_HTTP_STATUS_CODE);
	}

	protected ServerException(int code, String reason) {
		super(code, DEFAULT_MESSAGE);
		this.reason = reason;
		this.setHttpStatus(Constants.INTERNAL_SERVER_ERROR_HTTP_STATUS_CODE);
	}

	protected ServerException(int code, String reason, Throwable cause) {
		super(code, DEFAULT_MESSAGE, cause);
		this.reason = reason;
		this.setHttpStatus(Constants.INTERNAL_SERVER_ERROR_HTTP_STATUS_CODE);
	}

	public String getReason() {
		return reason;
	}

	@Override
	public void log(Logger logger) {
		logger.error(this.reason, this);
	}

	@Override
	public void log(Logger logger, Level level) {
		logger.atLevel(level)
			.setCause(this)
			.log(this.reason);
	}
}