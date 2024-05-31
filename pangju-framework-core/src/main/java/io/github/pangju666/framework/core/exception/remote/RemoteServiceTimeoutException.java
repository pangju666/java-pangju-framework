package io.github.pangju666.framework.core.exception.remote;

import io.github.pangju666.framework.core.exception.base.RemoteServiceException;
import io.github.pangju666.framework.core.lang.pool.ConstantPool;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.event.Level;

public class RemoteServiceTimeoutException extends RemoteServiceException {
	private static final String DEFAULT_MESSAGE = "远程服务调用超时";

	public RemoteServiceTimeoutException(String service, String api) {
		super(service, api, ConstantPool.REMOTE_SERVICE_TIMEOUT_ERROR_RESPONSE_CODE, DEFAULT_MESSAGE);
	}

	public RemoteServiceTimeoutException(String service, String api, String message) {
		super(service, api, ConstantPool.REMOTE_SERVICE_TIMEOUT_ERROR_RESPONSE_CODE, message);
	}

	public RemoteServiceTimeoutException(String service, String api, Throwable cause) {
		super(service, api, ConstantPool.REMOTE_SERVICE_TIMEOUT_ERROR_RESPONSE_CODE, DEFAULT_MESSAGE, cause);
	}

	public RemoteServiceTimeoutException(String service, String api, String message, Throwable cause) {
		super(service, api, ConstantPool.REMOTE_SERVICE_TIMEOUT_ERROR_RESPONSE_CODE, message, cause);
	}

	protected RemoteServiceTimeoutException(String service, String api, int code, String message) {
		super(service, api, code, message);
	}

	protected RemoteServiceTimeoutException(String service, String api, int code, String message, Throwable cause) {
		super(service, api, code, message, cause);
	}

	@Override
	public void log(Logger logger) {
		log(logger, Level.ERROR);
	}

	@Override
	public void log(Logger logger, Level level) {
		StringBuilder builder = new StringBuilder()
			.append("服务：")
			.append(this.getService())
			.append(" 接口：")
			.append(this.getApi());
		if (StringUtils.isNotBlank(getPath())) {
			builder.append(" 路径：").append(getPath());
		}
		builder.append(" 请求超时");
		logger
			.atLevel(level)
			.setCause(this)
			.log(builder.toString());
	}
}
