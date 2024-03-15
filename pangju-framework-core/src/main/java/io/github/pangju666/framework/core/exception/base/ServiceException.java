package io.github.pangju666.framework.core.exception.base;

import io.github.pangju666.framework.core.lang.pool.ConstantPool;

public class ServiceException extends BaseRuntimeException {
	public ServiceException(String message) {
		super(ConstantPool.SERVICE_ERROR_RESPONSE_CODE, message);
	}

	public ServiceException(String message, Throwable cause) {
		super(ConstantPool.SERVICE_ERROR_RESPONSE_CODE, message, cause);
	}

	protected ServiceException(int code, String message) {
		super(code, message);
	}

	protected ServiceException(int code, String message, Throwable cause) {
		super(code, message, cause);
	}

	protected ServiceException(int code, String message, int status) {
		super(code, message, status);
	}

	protected ServiceException(int code, String message, int status, Throwable cause) {
		super(code, message, status, cause);
	}
}
