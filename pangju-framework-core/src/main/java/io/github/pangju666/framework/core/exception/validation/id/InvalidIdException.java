package io.github.pangju666.framework.core.exception.validation.id;

import io.github.pangju666.framework.core.exception.validation.ValidationException;
import io.github.pangju666.framework.core.lang.pool.ConstantPool;

public class InvalidIdException extends ValidationException {
	public InvalidIdException() {
		super(ConstantPool.VALIDATION_ERROR_RESPONSE_CODE, "不是合法的id");
	}

	public InvalidIdException(String message) {
		super(ConstantPool.VALIDATION_ERROR_RESPONSE_CODE, message);
	}

	public InvalidIdException(String message, Throwable cause) {
		super(ConstantPool.VALIDATION_ERROR_RESPONSE_CODE, message, cause);
	}

	protected InvalidIdException(int code, String message) {
		super(code, message);
	}

	protected InvalidIdException(int code, String message, Throwable cause) {
		super(code, message, cause);
	}

	protected InvalidIdException(int code, String message, int status) {
		super(code, message, status);
	}

	protected InvalidIdException(int code, String message, int status, Throwable cause) {
		super(code, message, status, cause);
	}
}
