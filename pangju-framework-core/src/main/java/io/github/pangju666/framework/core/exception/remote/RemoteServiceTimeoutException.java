package io.github.pangju666.framework.core.exception.remote;

import io.github.pangju666.framework.core.exception.base.RemoteServiceException;
import io.github.pangju666.framework.core.lang.pool.ConstantPool;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.event.Level;

public class RemoteServiceTimeoutException extends RemoteServiceException {
	public RemoteServiceTimeoutException(String service, String apiPath, String message) {
		super(service, apiPath, message);
		this.setCode(ConstantPool.REMOTE_SERVICE_TIMEOUT_ERROR_RESPONSE_CODE);
	}

	public RemoteServiceTimeoutException(String service, String apiPath, String message, Throwable cause) {
		super(service, apiPath, message, cause);
		this.setCode(ConstantPool.REMOTE_SERVICE_TIMEOUT_ERROR_RESPONSE_CODE);
	}

	@Override
	public void log(Logger logger) {
		logger.error("服务：{}，接口路径：{}请求超时",
			StringUtils.defaultString(this.getService()),
			StringUtils.defaultString(this.getApiPath()));
	}

	@Override
	public void log(Logger logger, Level level) {
		logger.atLevel(level)
			.log("服务：{}，接口路径：{}请求超时",
				StringUtils.defaultString(this.getService()),
				StringUtils.defaultString(this.getApiPath())
			);
	}
}
