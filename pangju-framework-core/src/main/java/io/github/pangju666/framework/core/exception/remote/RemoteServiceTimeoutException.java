package io.github.pangju666.framework.core.exception.remote;

import io.github.pangju666.framework.core.exception.remote.model.RemoteServiceError;
import io.github.pangju666.framework.core.lang.pool.ConstantPool;
import org.slf4j.Logger;
import org.slf4j.event.Level;

public class RemoteServiceTimeoutException extends RemoteServiceException {
	protected static final String DEFAULT_MESSAGE = "远程服务请求超时";

	public RemoteServiceTimeoutException(RemoteServiceError remoteServiceError) {
		super(remoteServiceError, ConstantPool.REMOTE_SERVICE_TIMEOUT_ERROR_RESPONSE_CODE, DEFAULT_MESSAGE);
	}

	public RemoteServiceTimeoutException(RemoteServiceError remoteServiceError, String message) {
		super(remoteServiceError, ConstantPool.REMOTE_SERVICE_TIMEOUT_ERROR_RESPONSE_CODE, message);
	}

	public RemoteServiceTimeoutException(RemoteServiceError remoteServiceError, Throwable cause) {
		super(remoteServiceError, ConstantPool.REMOTE_SERVICE_TIMEOUT_ERROR_RESPONSE_CODE, DEFAULT_MESSAGE, cause);
	}

	public RemoteServiceTimeoutException(RemoteServiceError remoteServiceError, String message, Throwable cause) {
		super(remoteServiceError, ConstantPool.REMOTE_SERVICE_TIMEOUT_ERROR_RESPONSE_CODE, message, cause);
	}

	protected RemoteServiceTimeoutException(RemoteServiceError remoteServiceError, int code, String message) {
		super(remoteServiceError, code, message);
	}

	protected RemoteServiceTimeoutException(RemoteServiceError remoteServiceError, int code, String message, Throwable cause) {
		super(remoteServiceError, code, message, cause);
	}

	@Override
	public void log(Logger logger, Level level) {
		logger.atLevel(level).log("服务：{} 接口：｛｝ url：｛｝ 请求超时",
			this.getRemoteService().service(), this.getRemoteService().api(), this.getRemoteService().uri());
	}
}
