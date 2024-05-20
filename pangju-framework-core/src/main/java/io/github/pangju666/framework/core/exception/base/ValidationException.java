package io.github.pangju666.framework.core.exception.base;


import io.github.pangju666.framework.core.lang.pool.ConstantPool;

public class ValidationException extends BaseRuntimeException {
	public ValidationException(String message) {
		super(ConstantPool.VALIDATION_ERROR_RESPONSE_CODE, message);
	}

	public ValidationException(String message, Throwable cause) {
		super(ConstantPool.VALIDATION_ERROR_RESPONSE_CODE, message, cause);
	}

	public ValidationException(int code, String message) {
		super(code, message);
	}

	public ValidationException(int code, String message, Throwable cause) {
		super(code, message, cause);
	}
}
