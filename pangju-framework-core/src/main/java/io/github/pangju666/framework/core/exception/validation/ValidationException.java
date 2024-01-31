package io.github.pangju666.framework.core.exception.validation;


import io.github.pangju666.framework.core.exception.base.ServiceException;
import io.github.pangju666.framework.core.lang.pool.ConstantPool;

public class ValidationException extends ServiceException {
	public ValidationException() {
		super(ConstantPool.ERROR_VALIDATION_CODE, "参数校验失败");
	}

	public ValidationException(String message) {
		super(ConstantPool.ERROR_VALIDATION_CODE, message);
	}

	public ValidationException(String message, Throwable cause) {
		super(ConstantPool.ERROR_VALIDATION_CODE, message, cause);
	}

	public ValidationException(int code, String message) {
		super(code, message);
	}

	public ValidationException(int code, String message, Throwable cause) {
		super(code, message, cause);
	}

	protected ValidationException(int code, String message, int status) {
		super(code, message, status);
	}

	protected ValidationException(int code, String message, int status, Throwable cause) {
		super(code, message, status, cause);
	}
}
