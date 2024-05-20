package io.github.pangju666.framework.core.exception.base;

import io.github.pangju666.framework.core.lang.pool.ConstantPool;
import org.slf4j.Logger;
import org.slf4j.event.Level;

public class ServiceException extends BaseRuntimeException {
	private final String reason;

	public ServiceException(String message) {
		super(ConstantPool.SERVICE_ERROR_RESPONSE_CODE, message);
		this.reason = message;
	}

	public ServiceException(String message, String reason) {
		super(ConstantPool.SERVICE_ERROR_RESPONSE_CODE, message);
		this.reason = reason;
	}

	public ServiceException(String message, Throwable cause) {
		super(ConstantPool.SERVICE_ERROR_RESPONSE_CODE, message, cause);
		this.reason = message;
	}

	public ServiceException(String message, String reason, Throwable cause) {
		super(ConstantPool.SERVICE_ERROR_RESPONSE_CODE, message, cause);
		this.reason = reason;
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
