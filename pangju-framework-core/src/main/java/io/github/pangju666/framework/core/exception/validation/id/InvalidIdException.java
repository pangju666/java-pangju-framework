package io.github.pangju666.framework.core.exception.validation.id;

import io.github.pangju666.framework.core.exception.base.ValidationException;
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
}
