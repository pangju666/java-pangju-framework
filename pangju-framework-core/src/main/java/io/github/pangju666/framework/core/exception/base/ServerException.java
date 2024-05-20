package io.github.pangju666.framework.core.exception.base;

import io.github.pangju666.framework.core.lang.pool.ConstantPool;
import org.slf4j.Logger;
import org.slf4j.event.Level;

public class ServerException extends BaseRuntimeException {
	private static final String MESSAGE = "服务器内部错误";

	private final String reason;

	public ServerException() {
		super(ConstantPool.SERVER_ERROR_RESPONSE_CODE, MESSAGE);
		this.reason = MESSAGE;
		this.setHttpStatus(ConstantPool.INTERNAL_SERVER_ERROR_HTTP_STATUS_CODE);
	}

	public ServerException(String reason) {
		super(ConstantPool.SERVER_ERROR_RESPONSE_CODE, MESSAGE);
		this.reason = reason;
		this.setHttpStatus(ConstantPool.INTERNAL_SERVER_ERROR_HTTP_STATUS_CODE);
	}

	public ServerException(Throwable cause) {
		super(ConstantPool.SERVER_ERROR_RESPONSE_CODE, MESSAGE, cause);
		this.reason = MESSAGE;
		this.setHttpStatus(ConstantPool.INTERNAL_SERVER_ERROR_HTTP_STATUS_CODE);
	}

	public ServerException(String reason, Throwable cause) {
		super(ConstantPool.SERVER_ERROR_RESPONSE_CODE, MESSAGE, cause);
		this.reason = reason;
		this.setHttpStatus(ConstantPool.INTERNAL_SERVER_ERROR_HTTP_STATUS_CODE);
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
