package io.github.pangju666.framework.core.exception.validation.id;

import io.github.pangju666.framework.core.exception.base.ValidationException;
import io.github.pangju666.framework.core.lang.pool.ConstantPool;

public class IdExistException extends ValidationException {
	public IdExistException() {
		super(ConstantPool.VALIDATION_ERROR_RESPONSE_CODE, "id已存在");
	}

	public IdExistException(String message) {
		super(ConstantPool.VALIDATION_ERROR_RESPONSE_CODE, message);
	}

	public IdExistException(String message, Throwable cause) {
		super(ConstantPool.VALIDATION_ERROR_RESPONSE_CODE, message, cause);
	}
}
