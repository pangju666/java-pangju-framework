package io.github.pangju666.framework.web.exception;

import io.github.pangju666.framework.core.exception.base.ServiceException;
import io.github.pangju666.framework.core.lang.pool.ConstantPool;

public class RemoteServiceException extends ServiceException {
	public RemoteServiceException() {
		super(ConstantPool.REMOTE_ERROR_RESPONSE_CODE, "远程服务异常");
	}

	public RemoteServiceException(String message) {
		super(ConstantPool.REMOTE_ERROR_RESPONSE_CODE, message);
	}

	public RemoteServiceException(String message, Throwable cause) {
		super(ConstantPool.REMOTE_ERROR_RESPONSE_CODE, message, cause);
	}

	protected RemoteServiceException(int code, String message) {
		super(code, message);
	}

	protected RemoteServiceException(int code, String message, Throwable cause) {
		super(code, message, cause);
	}

	protected RemoteServiceException(int code, String message, int status) {
		super(code, message, status);
	}

	protected RemoteServiceException(int code, String message, int status, Throwable cause) {
		super(code, message, status, cause);
	}
}
