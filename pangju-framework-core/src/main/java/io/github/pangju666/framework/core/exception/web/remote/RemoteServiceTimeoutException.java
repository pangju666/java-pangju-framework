package io.github.pangju666.framework.core.exception.web.remote;

import io.github.pangju666.framework.core.exception.web.RemoteServiceException;

public class RemoteServiceTimeoutException extends RemoteServiceException {
	public RemoteServiceTimeoutException() {
		super(-44100, "远程服务连接超时");
	}

	public RemoteServiceTimeoutException(String message) {
		super(-44100, message);
	}

	public RemoteServiceTimeoutException(String message, Throwable cause) {
		super(-44100, message, cause);
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
