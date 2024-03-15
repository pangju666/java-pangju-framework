package io.github.pangju666.framework.web.exception.remote;

import io.github.pangju666.framework.core.lang.pool.ConstantPool;
import io.github.pangju666.framework.web.exception.RemoteServiceException;

public class RemoteServiceTimeoutException extends RemoteServiceException {
	public RemoteServiceTimeoutException() {
		super(ConstantPool.REMOTE_ERROR_RESPONSE_CODE, "远程服务连接超时");
	}

	public RemoteServiceTimeoutException(String message) {
		super(ConstantPool.REMOTE_ERROR_RESPONSE_CODE, message);
	}

	public RemoteServiceTimeoutException(String message, Throwable cause) {
		super(ConstantPool.REMOTE_ERROR_RESPONSE_CODE, message, cause);
	}

	protected RemoteServiceTimeoutException(int code, String message) {
		super(code, message);
	}

	protected RemoteServiceTimeoutException(int code, String message, Throwable cause) {
		super(code, message, cause);
	}

	protected RemoteServiceTimeoutException(int code, String message, int status) {
		super(code, message, status);
	}

	protected RemoteServiceTimeoutException(int code, String message, int status, Throwable cause) {
		super(code, message, status, cause);
	}
}
